package allen.utils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * java在linux环境下执行linux命令，然后返回命令返回值。
 * Created with IntelliJ IDEA.
 * User: zhengxu
 * Date: 13-11-20
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public class ExeShellCmdUtils {

    public static String exec(String cmd) {
        try {
            String[] cmdA = { "/bin/sh", "-c", cmd };
            Process process = Runtime.getRuntime().exec(cmdA);
            LineNumberReader br = new LineNumberReader(new InputStreamReader(
                    process.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line).append("\n");
            }

            return sb.length() > 0 ? sb.deleteCharAt(sb.length()-1).toString() : sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String pwdString = exec("pwd").toString();
        String netsString = exec("netstat -nat|grep -i \"80\"|wc -l").toString();

        System.out.println("==========获得值=============");
        System.out.println(pwdString);
        System.out.println(netsString);
    }

}