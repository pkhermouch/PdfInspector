package com.paul;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.XfdfReader;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.*;
import java.util.*;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        if (args.length < 1) {
            System.err.println("Please provide a PDF file name to open");
            System.exit(1);
        }

        String inputFileName = args[0];

        PdfReader pdfReader = new PdfReader(inputFileName);
        AcroFields form = pdfReader.getAcroFields();
        Map<String, AcroFields.Item> allFields = form.getAllFields();
        for (Map.Entry<String, AcroFields.Item> field : allFields.entrySet()) {
            String fieldName = field.getKey();
            AcroFields.Item fieldContents = field.getValue();
            int size = fieldContents.size();
            String fieldType = "Unknown";
            Set<PdfName> validFieldValues = new HashSet<>();
            for (int i = 0; i < size; i++) {
                PdfDictionary fieldDict = fieldContents.getValue(i);
                for (PdfName dictKey : fieldDict.getKeys()) {
                    if (dictKey.equals(PdfName.FT)) {
                        PdfObject dictVal = fieldDict.get(dictKey);
                        fieldType = dictVal.toString();
                    }
                    if (dictKey.equals(PdfName.AP)) {
                        // Top-level dictionaries (Table 8.19, page 614)
                        PdfDictionary apTopLevel = fieldDict.getAsDict(dictKey);
                        for (PdfName apKey : apTopLevel.getKeys()) {
                            PdfDictionary appearanceDict = apTopLevel.getAsDict(apKey);
                            validFieldValues.addAll(appearanceDict.getKeys());
                        }
                    }
                }
            }
            System.out.println("Field name: " + fieldName);
            System.out.println("Field type: " + fieldType);
            if (!validFieldValues.isEmpty()) {
                System.out.println("Valid field values: " + validFieldValues);
            }
        }
    }

    public static void printPdfObject(PdfObject obj) {
        printPdfObject(obj, 2);
    }
    
    public static void printPdfObject(PdfObject obj, int ind) {
        if (obj.isArray()) {
            PdfArray array = (PdfArray) obj;
            printIndent(ind);
            System.out.println("ARRAY VALUES");
            for (int i = 0; i < array.size(); i++) {
                printPdfObject(array.getPdfObject(i), ind + 2);
            }
        } else if (obj.isDictionary()) {
            PdfDictionary dict = (PdfDictionary) obj;
            for (PdfName key : dict.getKeys()) {
                printIndent(ind);
                System.out.println("KEY: " + key);
                printIndent(ind);
                System.out.println("VALUE");
                printPdfObject(dict.get(key), ind + 2);
            }
        } else {
            printIndent(ind);
            System.out.print("Type " + obj.type() + " ");
            System.out.println(obj);
        }
    }
    
    public static void printIndent(int ind) {
        for (int i = 0; i < ind; i++) {
            System.out.print(" ");
        }
    }

}
