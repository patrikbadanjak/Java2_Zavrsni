package hr.algebra.dal.documentation;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentationGenerator {

    private  static String ROOT_PACKAGE_NAME = "hr.algebra.dal.";
    private static String ROOT_PACKAGE_LOCATION = "D:\\OneDrive - Visoko uciliste Algebra\\Faks\\3. godina\\1. semestar\\Programiranje u Javi 2\\Zavrsni\\Data Access Layer\\src\\hr\\algebra\\dal\\";

    public static void generateDocumentation(FileWriter fileWriter) throws IOException, ClassNotFoundException {
        List<Path> filesInPackage = Files
                .walk(Paths.get(ROOT_PACKAGE_LOCATION))
                .filter(path -> !Files.isDirectory(path))
                .collect(Collectors.toList());

        for(Path filePath : filesInPackage) {
            String packageName = filePath.getParent().toFile().getName() + ".";
            String fileName = filePath.getFileName().toString();

            if (!fileName.substring(fileName.lastIndexOf(".")).equals(".fxml")) {
                if (fileName.indexOf(".") > 0)
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));

                if ("Main".equals(fileName))
                    packageName = "";

                Class<?> unknownObject = Class.forName(ROOT_PACKAGE_NAME + packageName + fileName);

                fileWriter.write("<h1 class='class-name'>Class name: " + unknownObject.getName()
                        + " </h1>");

                Field[] fields = unknownObject.getDeclaredFields();

                fileWriter.write("<h2 class='fields-title'>Fields:</h2>");
                fileWriter.write("<div class='fields'>");

                for (Field field : fields) {
                    Integer modifiers = field.getModifiers();
                    boolean isPublic = (modifiers % 2) == 1;
                    boolean isPrivate = (modifiers % 2) == 0;
                    if (isPublic) {
                        fileWriter.write("public ");
                    } else if (isPrivate) {
                        fileWriter.write("private ");
                    }
                    fileWriter.write(field.getType().getName() + " ");
                    fileWriter.write(field.getName() + "<br />");
                }
                fileWriter.write("</div>");
                fileWriter.write("<h2 class='constructors-title'>Constructors:</h2>");

                Constructor[] constructors = unknownObject.getConstructors();

                for (Constructor con : constructors) {
                    Parameter[] params = con.getParameters();

                    if (params.length > 0) {

                        fileWriter.write(
                                "<h3 class='constructors-parameters-title'>Constructor parameters: </h3>");

                        fileWriter.write(
                                "<div class='constructor-parameters'>");

                        for (Parameter param : params) {
                            fileWriter.write("Parameter: "
                                    + param.getType().getName());
                            fileWriter.write(" "
                                    + param.getName() + "<br />");
                        }

                        fileWriter.write("</div>");
                    } else {
                        fileWriter.write(
                                "<h3 class='constructors-title'>Default constructor without parameters"
                                        + "</h3>");
                    }
                }
            }
        }
    }
}
