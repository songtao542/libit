package com.liabit.build;

class BuildAsOne {

    static String[] mergeList = {
            "../viewbinding/src/main/java", // viewbinding
            "../widget/src/main/java", // widget
            "../recyclerview/src/main/java", // recyclerview
            "../timerview/src/main/java", // timerview
            "../ext/src/main/java", // ext
            "../popup/src/main/java", // popup
            "../citypicker/src/main/java", // citypicker
            "../photoview/src/main/java", // photoview
            "../picker/src/main/java", // picker
            "../dialog/src/main/java", // dialog
            "../picker_integrate/src/main/java" // picker_integrate
    };

    public static void main(String[] args) {
        String path = BuildAsOne.class.getResource("").getFile();
        int index = path.indexOf("build_as_one");
        String rootPath = path.substring(0, index + "build_as_one".length());
        for (String merge : mergeList) {
            System.out.println(rootPath);
        }
    }

}