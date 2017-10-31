package cn.hbjx.alib.util;

import java.util.ArrayList;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public class StringTookit {

    public StringTookit() {
    }

    public static String fieldToSetMethod(String fieldName) {
        String r = "set";
        char[] chars = fieldName.toCharArray();
        r = r + fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toUpperCase());
        return r;
    }

    public static String fieldToGetMethod(String fieldName) {
        String r = "get";
        char[] chars = fieldName.toCharArray();
        r = r + fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toUpperCase());
        return r;
    }

    public static String fieldToIsMethod(String fieldName) {
        String r = "is";
        char[] chars = fieldName.toCharArray();
        r = r + fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toUpperCase());
        return r;
    }

    public static String JSONStringFormat(String json) {
        String fillStringUnit = "\t";
        if(json != null && json.trim().length() != 0) {
            int fixedLenth = 0;
            ArrayList tokenList = new ArrayList();
            String buf = json;

            String count;
            while(buf.length() > 0) {
                count = getToken(buf);
                buf = buf.substring(count.length());
                count = count.trim();
                tokenList.add(count);
            }

            int i;
            for(int var10 = 0; var10 < tokenList.size(); ++var10) {
                count = (String)tokenList.get(var10);
                i = count.getBytes().length;
                if(i > fixedLenth && var10 < tokenList.size() - 1 && ((String)tokenList.get(var10 + 1)).equals(":")) {
                    fixedLenth = i;
                }
            }

            StringBuilder var11 = new StringBuilder();
            int var12 = 0;

            for(i = 0; i < tokenList.size(); ++i) {
                String token = (String)tokenList.get(i);
                if(token.equals(",")) {
                    var11.append(token);
                    doFill(var11, var12, fillStringUnit);
                } else if(token.equals(":")) {
                    var11.append(" ").append(token).append(" ");
                } else {
                    String var13;
                    if(token.equals("{")) {
                        var13 = (String)tokenList.get(i + 1);
                        if(var13.equals("}")) {
                            ++i;
                            var11.append("{ }");
                        } else {
                            ++var12;
                            var11.append(token);
                            doFill(var11, var12, fillStringUnit);
                        }
                    } else if(token.equals("}")) {
                        --var12;
                        doFill(var11, var12, fillStringUnit);
                        var11.append(token);
                    } else if(token.equals("[")) {
                        var13 = (String)tokenList.get(i + 1);
                        if(var13.equals("]")) {
                            ++i;
                            var11.append("[ ]");
                        } else {
                            ++var12;
                            var11.append(token);
                            doFill(var11, var12, fillStringUnit);
                        }
                    } else if(token.equals("]")) {
                        --var12;
                        doFill(var11, var12, fillStringUnit);
                        var11.append(token);
                    } else {
                        var11.append(token);
                        if(i < tokenList.size() - 1 && ((String)tokenList.get(i + 1)).equals(":")) {
                            int fillLength = fixedLenth - token.getBytes().length;
                            if(fillLength > 0) {
                                for(int j = 0; j < fillLength; ++j) {
                                    var11.append(" ");
                                }
                            }
                        }
                    }
                }
            }

            return var11.toString();
        } else {
            return null;
        }
    }

    private static String getToken(String json) {
        StringBuilder buf = new StringBuilder();
        boolean isInYinHao = false;

        while(json.length() > 0) {
            String token = json.substring(0, 1);
            json = json.substring(1);
            if(!isInYinHao && (token.equals(":") || token.equals("{") || token.equals("}") || token.equals("[") || token.equals("]") || token.equals(","))) {
                if(buf.toString().trim().length() == 0) {
                    buf.append(token);
                }
                break;
            }

            if(token.equals("\\")) {
                buf.append(token);
                buf.append(json.substring(0, 1));
                json = json.substring(1);
            } else if(token.equals("\"")) {
                buf.append(token);
                if(isInYinHao) {
                    break;
                }

                isInYinHao = true;
            } else {
                buf.append(token);
            }
        }

        return buf.toString();
    }

    private static void doFill(StringBuilder buf, int count, String fillStringUnit) {
        buf.append("\n");

        for(int i = 0; i < count; ++i) {
            buf.append(fillStringUnit);
        }

    }

}
