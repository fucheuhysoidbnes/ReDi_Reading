package com.example.redi.common.utils;

public class DriveUtils {

    public static String getDirectImageLink(String url) {
        String id = extractDriveId(url);
        if (id == null) return url;
        return "https://drive.google.com/uc?export=view&id=" + id;
    }

    public static String getPdfPreviewLink(String url) {
        String id = extractDriveId(url);
        if (id == null) return url;
        return "https://drive.google.com/file/d/" + id + "/preview";
    }

    public static String extractDriveId(String url) {
        if (url == null) return null;

        try {
            if (url.contains("/file/d/")) {
                return url.split("/d/")[1].split("/")[0];
            }
            if (url.contains("id=")) {
                return url.split("id=")[1].split("&")[0];
            }
            if (url.contains("open?id=")) {
                return url.split("open?id=")[1];
            }
            if (url.contains("/uc?export=view&id=")) {
                return url.split("id=")[1];
            }
        } catch (Exception ignored) {}

        return null;
    }
}
