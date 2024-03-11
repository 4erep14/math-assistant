package org.example.utils;

public class StringToDoubleConverter {
    public static double convert(String value) {
        try {
            if (value.contains("/")) {
                String[] parts = value.split("/");
                if(parts.length > 2) {
                    throw new NumberFormatException();
                }
                return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            System.out.println("The root of equation should be a number");
            throw ex;
        }
    }
}
