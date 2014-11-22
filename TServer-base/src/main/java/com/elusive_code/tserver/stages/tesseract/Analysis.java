package com.elusive_code.tserver.stages.tesseract;

/**
 * @author Vladislav Dolgikh
 */
public enum Analysis {
    OSD_ONLY("Orientation and script detection(OSD) only"),
    APS_OSD("Automatic page segmentation(APS) with OSD "),
    APS_ONLY("Automatic page segmentation, but no OSD, or OCR"),
    APS("Fully automatic page segmentation, but no OSD (default)"),
    COLUMN("Assume a single column of text of variable sizes"),
    COLUMN_VERT("Assume a single uniform block of vertically aligned text"),
    UNIFORM("Assume a single uniform block of text"),
    LINE("Treat the image as a single text line"),
    WORD("Treat the image as a single word"),
    WORD_IN_CIRCLE("Treat the image as a single word in a circle"),
    CHARACTER("Treat the image as a single character")
    ;


    private String description;

    Analysis(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
