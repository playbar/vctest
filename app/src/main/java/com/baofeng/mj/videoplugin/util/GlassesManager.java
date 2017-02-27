package com.baofeng.mj.videoplugin.util;

import android.content.Context;
import android.text.TextUtils;

import com.baofeng.mj.videoplugin.bean.GlassesItemMode;
import com.baofeng.mj.videoplugin.bean.GlassesTypeMode;
import com.baofeng.mojing.MojingSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muyu on 2016/5/19.
 */
public class GlassesManager {

    private Context mContext;
    private static GlassesManager instance;

    public static List<GlassesItemMode> datas = null;

    private GlassesManager(Context context) {
        mContext = context;
    }

    public static GlassesManager getInstance(Context context) {
        if (instance == null) {
            instance = new GlassesManager(context);
        }
        if (!MojingSDK.GetInitSDK()) {
            MojingSDK.Init(context);
        }

        init();

        return instance;
    }

    private static void init() {
        List<GlassesItemMode> lists = getItemList();
    }

    /**
     * 解析json数据
     *
     * @param json
     * @param typeArray 解析的数组类型
     * @return 列表
     */
    private static List<GlassesTypeMode> parserJson(String json, String typeArray) {
        if (TextUtils.isEmpty(json))
            return null;
        ArrayList<GlassesTypeMode> lists = new ArrayList<GlassesTypeMode>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            String className = "";
            if (jsonObj.has("ClassName")) {
                className = jsonObj.getString("ClassName");
            }
            String releaseDate = "";
            if (jsonObj.has("ReleaseDate")) {
                releaseDate = jsonObj.getString("ReleaseDate");
            }
            JSONArray arry = jsonObj.getJSONArray(typeArray);
            if (arry != null && arry.length() > 0) {
                for (int i = 0; i < arry.length(); i++) {
                    JSONObject obj = arry.getJSONObject(i);
                    String display = "";
                    if (obj.has("Display")) {
                        display = obj.getString("Display");
                    }
                    String url = "";
                    if (obj.has("URL")) {
                        url = obj.getString("URL");
                    }
                    String key = obj.getString("KEY");
                    String id = "";
                    if (obj.has("ID")) {
                        id = obj.getString("ID");
                    }
                    GlassesTypeMode model = new GlassesTypeMode(className, releaseDate, display, url, key, id);
                    lists.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    /**
     * SDK中所有的眼镜信息
     */
    private static List<GlassesItemMode> getItemList() {
        if (datas != null && datas.size() > 0)
            return datas;
        String json = MojingSDK.GetManufacturerList("ZH");
        if (!TextUtils.isEmpty(json) && json.contains("ERROR")) {
            return null;
        }
        datas = new ArrayList<GlassesItemMode>();
        List<GlassesTypeMode> manuList = parserJson(json, "ManufacturerList");
        if (manuList == null || manuList.size() <= 0)
            return datas;
        for (GlassesTypeMode typemode : manuList) {
            if (typemode == null)
                continue;
            String producJson = MojingSDK.GetProductList(typemode.getKEY(), "ZH");
            if (!TextUtils.isEmpty(producJson) && producJson.contains("ERROR")) {
                continue;
            }
            List<GlassesTypeMode> productData = parserJson(producJson, "ProductList");

            if (productData == null || productData.size() <= 0)
                continue;
            for (GlassesTypeMode producmode : productData) {
                if (producmode == null)
                    continue;
                String glassJson = MojingSDK.GetGlassList(producmode.getKEY(), "ZH");
                if (!TextUtils.isEmpty(glassJson) && glassJson.contains("ERROR")) {
                    continue;
                }
                List<GlassesTypeMode> glassesListdata = parserJson(glassJson, "GlassList");
                if (glassesListdata == null || glassesListdata.size() <= 0)
                    continue;
                for (GlassesTypeMode glassesMode : glassesListdata) {
                    if (glassesMode == null)
                        continue;

                    GlassesItemMode itemMode = new GlassesItemMode();
                    itemMode.setManufacturerName(typemode.getDisplay());
                    itemMode.setProductName(producmode.getDisplay());
                    itemMode.setProductKey(producmode.getKEY());
                    itemMode.setGlassesName(glassesMode.getDisplay());
                    itemMode.setGlassesKey(glassesMode.getKEY());
                    itemMode.setGlassesID(glassesMode.getID());
                    itemMode.setProductID(producmode.getID());
                    itemMode.setManufactureID(typemode.getID());
                    datas.add(itemMode);
                }
            }
        }
        return datas;
    }

    public String getKeyFromIds(String manufactureid, String productid, String glassesid){
        if(datas == null){
            return "";
        }
        for (GlassesItemMode mode : datas) {
            if (mode.getManufactureID().equals(manufactureid) && mode.getProductID().equals(productid) && mode.getGlassesID().equals(glassesid)) {
                return mode.getGlassesKey();
            }
        }
        return "";
    }

}
