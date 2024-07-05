import excelutility.ExcelUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

import static constants.Constants.bannerPath;

public class GeneratorMain {

    private static final Logger logger = LogManager.getLogger(GeneratorMain.class);
    private static void printBanner() {
        try (InputStream inputStream = GeneratorMain.class.getResourceAsStream(bannerPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Failed to read the banner file: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Banner file not found: " + bannerPath);
        }
    }

    public static void main(String[] args){
        printBanner();
        if (args.length != 2) {
            logger.error("Please provide command : java -jar OBELCM-sqlGenerator-v1.jar <input-directory> <output-directory>");
            System.exit(1);
        }
        String inputDir = args[0];
        String outputDir = args[1];
        logger.info("******** IDEMPOTENT SCRIPT GENERATOR ********");
        ExcelUtility excelUtility = new ExcelUtility();
        logger.info("Input Directory : "+inputDir);
        logger.info("Output Directory : "+outputDir);
        excelUtility.preProcessing(inputDir,outputDir);
    }

}
