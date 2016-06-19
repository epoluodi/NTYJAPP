package com.suypower.pms.app;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 信息会犯处理类
 * @author YXG
 */
public class ReturnData  {



    String returnMsg;
    JSONObject returnData;
    JSONArray jsonArray;
    int returnCode;




    public ReturnData(JSONObject jsonObject,Boolean jsobj) throws Exception
    {

        try
        {
            returnCode = jsonObject.getInt("status");

            returnMsg =jsonObject.getString("msg");
            if (!jsonObject.isNull("data")) {
                if (jsobj)

                    returnData = jsonObject.getJSONObject("data");
                else
                    jsonArray = jsonObject.getJSONArray("data");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public JSONObject getReturnData() {
        return returnData;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
