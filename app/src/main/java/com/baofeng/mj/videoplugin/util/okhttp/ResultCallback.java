package com.baofeng.mj.videoplugin.util.okhttp;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by muyu on 2016/11/1.
 */
public abstract class ResultCallback<T> {

    public RequestCallBack requestCallBack;
    Type mType;

    public ResultCallback(RequestCallBack requestCallBack) {
        mType = getSuperclassTypeParameter(getClass());
        this.requestCallBack = requestCallBack;
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        //return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        return parameterized.getActualTypeArguments()[0];
    }

    public abstract T parseResponse(String responseString);

//    public abstract void onError(Request request, Exception e);
//
//    public abstract void onResponse(T response);

}
