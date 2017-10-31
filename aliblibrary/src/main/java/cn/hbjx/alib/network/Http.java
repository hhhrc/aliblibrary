package cn.hbjx.alib.network;

import cn.hbjx.alib.util.Lg;

import java.io.*;
import java.net.*;
import java.util.*;

public class Http {
    public enum NetType {
        HTTP("http"), HTTPS("https"), WEBSERVICE("webservice");
        private String context;

        private NetType(String context) {
            this.context = context;
        }

        public String getContext() {
            return context;
        }
    }

    public enum DataType {
        TYPE1_FORM("multipart/form-data"), TYPE2_URLENCODED("application/x-www-form-urlencoded"), TYPE3_RAW("multipart/form-data");
        private String context;

        private DataType(String context) {
            this.context = context;
        }

        public String getContext() {
            return context;
        }
    }

    public static String GET = "get";
    public static String POST = "post";
    private String type = "get";
    private String url;
    private String charset = "utf-8";
    private DataType dataType = DataType.TYPE2_URLENCODED;
    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> headers = new HashMap<String, String>();
    // private Map<String, File> files = new HashMap<String, File>();
    private List<MyFile> filelist = new ArrayList<MyFile>();
    private Set<Map.Entry<String, String>> entrys;
    private HttpURLConnection conn = null;
    private OutputStream out = null;
    private BufferedReader reader = null;
    private Proxy proxy = null;
    String BOUNDARY = UUID.randomUUID().toString();
    String PREFIX = "--";
    String LINEND = "\r\n";
    String CHARSET = "UTF-8";
    private boolean isSppend = true;

    public Http(String url, String type) {
        this.url = url;
        this.setType(type);
    }

    public void isSppend(boolean isSppend){
        this.isSppend = isSppend;
    }

    public void setProxy(String address, int port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
    }

    public void setProxy(String address, String port) {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address,
                Integer.parseInt(port)));
    }

    public Http(String url) {
        this.url = url;
    }

    public void setType(String type) {
        this.type = type.trim().toLowerCase();
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    private void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void addFile(String filepath) {
        type = "post";
        // files.put(filepath.substring(filepath.lastIndexOf("/")), new File(
        // filepath));
        MyFile file = new MyFile();
        if (filepath == null || "".equals(filepath)) {
            file.filePath = "file";
            file.myFile = null;
            if (Lg.DEBUG) {
                System.out.print("file.filePath=====file");
            }
        } else {
            file.filePath = filepath.substring(filepath.lastIndexOf("/"));
            file.myFile = new File(filepath);
        }
        filelist.add(file);
    }

    public void addFile(String filepath, String key) {
        type = "post";
        // files.put(filepath.substring(filepath.lastIndexOf("/")), new File(
        // filepath));
        MyFile file = new MyFile();
        if (filepath == null || "".equals(filepath)) {
            file.filePath = "file";
            file.myFile = null;
            if (Lg.DEBUG) {
                System.out.print("file.filePath=====file");
            }
        } else {
            file.filePath = filepath.substring(filepath.lastIndexOf("/"));
            file.myFile = new File(filepath);
        }
        file.name = key;
        filelist.add(file);
    }

    private class MyFile {
        public String filePath;
        public File myFile;
        public String name = "file";
    }

    public String execute() {
        String s = null;
        try {
            s = execute("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public String executeFormatJSON(String charset) {
        return JSONStringFormat(execute(charset));
    }

    public String executeFormatJSON() {
        return JSONStringFormat(execute("utf-8"));
    }

    public String execute(String charset) {
        this.charset = charset;
        String res = null;
        if (type.equals("get")) {
            try {
                res = doGet();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return res;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
                return res;
            }
        } else if (type.equals("post")) {
            try {
                if (dataType == DataType.TYPE1_FORM) {
                    res = doPost_form_data();
                } else if (dataType == DataType.TYPE2_URLENCODED) {
                    res = doPost_urlencoded();
                } else if (dataType == DataType.TYPE3_RAW) {
                    res = doPost_raw();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return res;
            }
        }
        return res;
    }

    private String doPost_raw() {
        // TODO Auto-generated method stub
        return null;
    }

    private String doPost_urlencoded() throws Exception {
        URL url1 = new URL(url);
        if (proxy == null) {
            conn = (HttpURLConnection) url1.openConnection();
        } else {
            conn = (HttpURLConnection) url1.openConnection(proxy);
        }
        // 请求参数
        StringBuilder buf = new StringBuilder();
        if (params.size() > 0 && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buf.append(entry.getKey());
                buf.append("=");
                buf.append(entry.getValue());
                buf.append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }
//		Lg.println(buf.toString());
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(buf.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            outputStream = conn.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write(buf.toString());
            outputStreamWriter.flush();

            if (conn.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }

            inputStream = conn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    private String doPost_form_data() throws IOException {
        String result = null;
        URL url1 = new URL(url);
        if (proxy == null) {
            conn = (HttpURLConnection) url1.openConnection();
        } else {
            conn = (HttpURLConnection) url1.openConnection(proxy);
        }
        initConn(conn);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", dataType.context
                + ";boundary=" + BOUNDARY);
        out = conn.getOutputStream();

        // 请求参数
        StringBuilder buf = new StringBuilder();
        if (isSppend && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buf.append(PREFIX);
                buf.append(BOUNDARY);
                buf.append(LINEND);
                buf.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                buf.append("Content-Type: text/plain; charset=" + CHARSET
                        + LINEND);
                buf.append("Content-Transfer-Encoding: 8bit" + LINEND);
                buf.append(LINEND);
                buf.append(entry.getValue());
                buf.append(LINEND);
            }
        } else if (params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buf.append(entry.getValue());
            }
        }
        out.write(buf.toString().getBytes("UTF-8"));
        out.flush();
        // 发送请求头
        if (headers != null && headers.size() > 0) {
            entrys = headers.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        // 发送文件数据
        // if (files != null && files.size() > 0)
        // for (Map.Entry<String, File> file : files.entrySet()) {
        // StringBuilder sb1 = new StringBuilder();
        // sb1.append(PREFIX);
        // sb1.append(BOUNDARY);
        // sb1.append(LINEND);
        // sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
        // + file.getKey() + "\"" + LINEND);
        // sb1.append("Content-Type: application/octet-stream; charset="
        // + CHARSET + LINEND);
        // sb1.append(LINEND);
        // out.write(sb1.toString().getBytes());
        //
        // InputStream is = new FileInputStream(file.getValue());
        // byte[] buffer = new byte[1024];
        // int len = 0;
        // while ((len = is.read(buffer)) != -1) {
        // out.write(buffer, 0, len);
        // }
        //
        // is.close();
        // out.write(LINEND.getBytes());
        // }
        if (filelist != null && filelist.size() > 0) {
            for (int i = 0; i < filelist.size(); i++) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\""
                        + filelist.get(i).name + "\"; filename=\""
                        + filelist.get(i).filePath + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                out.write(sb1.toString().getBytes());
                InputStream is = null;
                if (filelist.get(i).myFile == null) {
                    is = new FileInputStream("");
                } else {
                    is = new FileInputStream(filelist.get(i).myFile);
                }
                byte[] buffer = new byte[1024];
                int len = 0;

                while (Thread.currentThread().isInterrupted() == false && (len = is.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                is.close();
                out.write(LINEND.getBytes());
            }
        }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        out.write(end_data);
        out.flush();
        conn.connect();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // 接收返回请求
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), charset));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
            reader.close();
        }
        out.close();
        conn.disconnect();
        return result;
    }

    private String doGet() throws IOException {
        String result = null;
        StringBuilder buf = new StringBuilder(url);
        if (params.size() > 0) {
            buf.append("?");
            entrys = params.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                buf.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            buf.deleteCharAt(buf.length() - 1);
        }

        URL url1 = new URL(buf.toString());
        if (proxy == null) {
            conn = (HttpURLConnection) url1.openConnection();
        } else {
            conn = (HttpURLConnection) url1.openConnection(proxy);
        }
        initConn(conn);
        conn.setRequestMethod("GET");
        if (headers != null && headers.size() > 0) {
            entrys = headers.entrySet();
            for (Map.Entry<String, String> entry : entrys) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 接收返回请求
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), charset));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        }
        reader.close();
        conn.disconnect();
        return result;
    }

    public String getChartset() {
        return charset;
    }

    public void setChartset(String chartset) {
        this.charset = chartset;
    }

    private void initConn(HttpURLConnection conn) {
        conn.setRequestProperty("Content-type",
                "application/x-java-serialized-object");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setConnectTimeout(1000 * 20);
        conn.setReadTimeout(1000 * 30);
    }

    public void cancel() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            conn.disconnect();
        }

        if (Lg.DEBUG) {
            Lg.println("取消接口调用");
        }

    }

    /**
     * JSON 格式化输出===============================
     */
    public static String JSONStringFormat(String json) {
        String fillStringUnit = "\t";
        if (json == null || json.trim().length() == 0) {
            return null;
        }

        int fixedLenth = 0;
        ArrayList<String> tokenList = new ArrayList<String>();
        {
            String jsonTemp = json;
            // Ԥ��ȡ
            while (jsonTemp.length() > 0) {
                String token = getToken(jsonTemp);
                jsonTemp = jsonTemp.substring(token.length());
                token = token.trim();
                tokenList.add(token);
            }
        }

        for (int i = 0; i < tokenList.size(); i++) {
            String token = tokenList.get(i);
            int length = token.getBytes().length;
            if (length > fixedLenth && i < tokenList.size() - 1
                    && tokenList.get(i + 1).equals(":")) {
                fixedLenth = length;
            }
        }

        StringBuilder buf = new StringBuilder();
        int count = 0;
        for (int i = 0; i < tokenList.size(); i++) {

            String token = tokenList.get(i);

            if (token.equals(",")) {
                buf.append(token);
                doFill(buf, count, fillStringUnit);
                continue;
            }
            if (token.equals(":")) {
                buf.append(" ").append(token).append(" ");
                continue;
            }
            if (token.equals("{")) {
                String nextToken = tokenList.get(i + 1);
                if (nextToken.equals("}")) {
                    i++;
                    buf.append("{ }");
                } else {
                    count++;
                    buf.append(token);
                    doFill(buf, count, fillStringUnit);
                }
                continue;
            }
            if (token.equals("}")) {
                count--;
                doFill(buf, count, fillStringUnit);
                buf.append(token);
                continue;
            }
            if (token.equals("[")) {
                String nextToken = tokenList.get(i + 1);
                if (nextToken.equals("]")) {
                    i++;
                    buf.append("[ ]");
                } else {
                    count++;
                    buf.append(token);
                    doFill(buf, count, fillStringUnit);
                }
                continue;
            }
            if (token.equals("]")) {
                count--;
                doFill(buf, count, fillStringUnit);
                buf.append(token);
                continue;
            }

            buf.append(token);
            // �����
            if (i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":")) {
                int fillLength = fixedLenth - token.getBytes().length;
                if (fillLength > 0) {
                    for (int j = 0; j < fillLength; j++) {
                        buf.append(" ");
                    }
                }
            }
        }
        return buf.toString();
    }

    private static String getToken(String json) {
        StringBuilder buf = new StringBuilder();
        boolean isInYinHao = false;
        while (json.length() > 0) {
            String token = json.substring(0, 1);
            json = json.substring(1);

            if (!isInYinHao
                    && (token.equals(":") || token.equals("{")
                    || token.equals("}") || token.equals("[")
                    || token.equals("]") || token.equals(","))) {
                if (buf.toString().trim().length() == 0) {
                    buf.append(token);
                }

                break;
            }

            if (token.equals("\\")) {
                buf.append(token);
                buf.append(json.substring(0, 1));
                json = json.substring(1);
                continue;
            }
            if (token.equals("\"")) {
                buf.append(token);
                if (isInYinHao) {
                    break;
                } else {
                    isInYinHao = true;
                    continue;
                }
            }
            buf.append(token);
        }
        return buf.toString();
    }

    private static void doFill(StringBuilder buf, int count,
                               String fillStringUnit) {
        buf.append("\n");
        for (int i = 0; i < count; i++) {
            buf.append(fillStringUnit);
        }
    }

    /**
     * JSON 格式化_______________________________
     */

    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     *
     * @return
     * @throws IOException
     */
    // public String post(String actionUrl, Map<String, String> params,
    // Map<String, File> files) throws IOException {
    // URL uri = new URL(actionUrl);
    // HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
    // conn.setReadTimeout(60 * 1000); // 缓存的最长时间
    // conn.setDoInput(true);// 允许输入
    // conn.setDoOutput(true);// 允许输出
    // conn.setUseCaches(false); // 不允许使用缓存
    // conn.setRequestMethod("POST");
    // conn.setRequestProperty("connection", "keep-alive");
    // conn.setRequestProperty("Charsert", "UTF-8");
    // conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
    // + ";boundary=" + BOUNDARY);
    //
    // // 首先组拼文本类型的参数
    // StringBuilder sb = new StringBuilder();
    // for (Map.Entry<String, String> entry : params.entrySet()) {
    // sb.append(PREFIX);
    // sb.append(BOUNDARY);
    // sb.append(LINEND);
    // sb.append("Content-Disposition: form-data; name=\""
    // + entry.getKey() + "\"" + LINEND);
    // sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
    // sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
    // sb.append(LINEND);
    // sb.append(entry.getValue());
    // sb.append(LINEND);
    // }
    //
    // DataOutputStream outStream = new DataOutputStream(
    // conn.getOutputStream());
    // outStream.write(sb.toString().getBytes());
    // // 发送文件数据
    // if (files != null)
    // for (Map.Entry<String, File> file : files.entrySet()) {
    // StringBuilder sb1 = new StringBuilder();
    // sb1.append(PREFIX);
    // sb1.append(BOUNDARY);
    // sb1.append(LINEND);
    // sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
    // + file.getKey() + "\"" + LINEND);
    // sb1.append("Content-Type: application/octet-stream; charset="
    // + CHARSET + LINEND);
    // sb1.append(LINEND);
    // outStream.write(sb1.toString().getBytes());
    //
    // InputStream is = new FileInputStream(file.getValue());
    // byte[] buffer = new byte[1024];
    // int len = 0;
    // while ((len = is.read(buffer)) != -1) {
    // outStream.write(buffer, 0, len);
    // }
    //
    // is.close();
    // outStream.write(LINEND.getBytes());
    // }
    //
    // // 请求结束标志
    // byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
    // outStream.write(end_data);
    // outStream.flush();
    // // 得到响应码
    // String result = null;
    // int responseCode = conn.getResponseCode();
    // if (responseCode == HttpURLConnection.HTTP_OK) {
    // // 接收返回请求
    // reader = new BufferedReader(new InputStreamReader(
    // conn.getInputStream(), charset));
    // String line = "";
    // StringBuffer buffer = new StringBuffer();
    // while ((line = reader.readLine()) != null) {
    // buffer.append(line);
    // }
    // result = buffer.toString();
    // }
    // reader.close();
    // conn.disconnect();
    // return result;
    // }
    public static boolean testProxy(String address, String port) {
        try {
            long startNanoTime = System.nanoTime();
            Http h = new Http("http://www.ip138.com/ips138.asp");
            h.setProxy(address, port);
            String result = h.execute("gbk");
            int a = result.indexOf("您的IP地址是");
            int b = result.indexOf("<br/><br/></td>");
            result = result.substring(a, b);
            double timeSplit = (System.nanoTime() - startNanoTime) / 1000000000.0;
            Lg.println(result);
            Lg.println(timeSplit + "秒");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean testProxy(String url, String address, String port) {
        try {
            long startNanoTime = System.nanoTime();
            Http h = new Http(url);
            h.setProxy(address, port);
            String result = h.execute();
            double timeSplit = (System.nanoTime() - startNanoTime) / 1000000000.0;
            Lg.println(result);
            Lg.println(timeSplit + "秒");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ URL -> ").append(url).append(" ]\n");
        sb.append("[ TYPE -> ").append(type).append(" ]\n");
        sb.append("[ DATA_TYPE -> ").append(dataType.getContext()).append(" ]\n");
        sb.append("[ PARAMS-> \n");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("           ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
}

