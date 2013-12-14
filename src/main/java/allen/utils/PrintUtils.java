package allen.utils;

/**
 */
public class PrintUtils {

    private static boolean isTest(){
          return true;
    }

    public static void soutIfTest(String s) {
        if (isTest()) {
            System.out.println(s);
        }
    }

    public static void serrInTest(String s) {
        if (isTest()) {
            System.err.println(s);
        }
    }
}