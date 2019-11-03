import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProperyAnalizer {

    public static void main(String[] args) throws Exception {
        boolean test = false;

        String currentPropFileName = "currentPropertyFile.json";
        String currentFileToCompareWithName = "mergeOfAEMResponses.json";

        Set<String> propertiesInBothFilesSet = new HashSet<>();
        Set<String> propertiesOnlyInCurrentSet = new HashSet<String>();

        if (test){
            System.out.println("=============  THIS IS A TEST RUN ===============");
            currentPropFileName = "test.json";
            currentFileToCompareWithName = "test1.json";
        }

        File currentPropFile = new File("resources/" + currentPropFileName).getAbsoluteFile();
        File EOMResponseFile = new File("resources/" + currentFileToCompareWithName).getAbsoluteFile();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> currentPropMap = new JsonFlattener(mapper.readTree(currentPropFile).toString()).flattenAsMap();
        Map<String, Object> EOMRespomseMap = new JsonFlattener(mapper.readTree(EOMResponseFile).toString()).flattenAsMap();
        Map<String, Object> EOMRespomseGlobalMap = filterGlobalMap(EOMRespomseMap, "global.");
        Map<String, Object> EOMRespomseCountriesMap = filterGlobalMap(EOMRespomseMap, "countries.");
        Map<String, Object> EOMRespomseMessagessMap = filterGlobalMap(EOMRespomseMap, "messages.");
        Map<String, Object> EOMRespomseProductsMap = filterGlobalMap(EOMRespomseMap, "products.");
        int propertiesInBothFiles = 0;

        for (Map.Entry<String, Object> node : currentPropMap.entrySet()) {
            String keyToCheck =  node.getKey();
            keyToCheck = keyToCheck.substring(3, keyToCheck.length()-3);
            String propertyName = node.getKey().substring(3, node.getKey().length()-3);

            //check global
            if (checkKeysInMapIgnoreCase(EOMRespomseGlobalMap, keyToCheck)) {
                propertiesInBothFilesSet.add(propertyName);
                propertiesInBothFiles++;
            } else {
                propertiesOnlyInCurrentSet.add(propertyName);
            }

            //check countries
            if (checkKeysInMapIgnoreCase(EOMRespomseCountriesMap, keyToCheck)) {
                propertiesInBothFilesSet.add(propertyName);
                propertiesInBothFiles++;
            } else {
                propertiesOnlyInCurrentSet.add(propertyName);
            }

            //check messages
            if (checkKeysInMapIgnoreCase(EOMRespomseMessagessMap, keyToCheck)) {
                propertiesInBothFilesSet.add(propertyName);
                propertiesInBothFiles++;
            } else {
                propertiesOnlyInCurrentSet.add(propertyName);
            }

            //check products
            if (checkKeysInMapIgnoreCase(EOMRespomseProductsMap, keyToCheck)) {
                propertiesInBothFilesSet.add(propertyName);
                propertiesInBothFiles++;
            } else {
                propertiesOnlyInCurrentSet.add(propertyName);
            }

        }

        System.out.println(propertiesOnlyInCurrentSet);

        System.out.println("Quantity of properties in current file: " + currentPropMap.entrySet().size());
        System.out.println("Quantity of properties in both files: " + (propertiesInBothFiles));
    }

    private static Map<String, Object> filterGlobalMap(Map<String, Object> eomRespomseMap, String sufix) {
        Map<String, Object>  mapToReturn = new HashMap<String, Object>();
        for (String key : eomRespomseMap.keySet()) {
            String sufixKey = key.substring(0, sufix.length());
            if (sufixKey.equals(sufix)){
                mapToReturn.put(key, eomRespomseMap.get(key));
            }
        }
        return mapToReturn;
    }

    private static boolean checkKeysInMapIgnoreCase(Map<String, Object> EOMResponseMap, String s) {
        for (String EOMkey : EOMResponseMap.keySet()){
            String uppercaseEOMKey = EOMkey.toUpperCase();
            String uppercaseS = s.toUpperCase();

            uppercaseEOMKey = checkIfThereAreAvailableTransformations(uppercaseEOMKey, s);

            if (uppercaseEOMKey.equals(uppercaseS)){
                return true;
            }
        }
        return false;
    }

    private static String checkIfThereAreAvailableTransformations(String uppercaseEOMKey, String s) {
        if (s.contains("PRODUCTS.REGULARSHIPMENT.")){
            int a =2;
        }
        if (uppercaseEOMKey.contains("PRODUCTS.REGULARSHIPMENT.")){
            return uppercaseEOMKey.replace("PRODUCTS.REGULARSHIPMENT.", "MODALREGULARSHIPMENT.");
        }
        if (uppercaseEOMKey.contains("MESSAGES.VALIDATIONSVALUE.")){
            return uppercaseEOMKey.replace("MESSAGES.VALIDATIONSVALUE.", "VALIDATIONS.");
        }


        return uppercaseEOMKey;
    }
}