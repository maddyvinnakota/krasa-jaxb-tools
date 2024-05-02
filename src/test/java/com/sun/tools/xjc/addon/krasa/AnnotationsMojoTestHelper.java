package com.sun.tools.xjc.addon.krasa;

/**
 *
 * @author Francesco Illuminati
 */
public class AnnotationsMojoTestHelper extends RunXJC2MojoTestHelper {

    private final String folderName;
    private final JaxbValidationsAnnotation annotation;

    public AnnotationsMojoTestHelper(String folderName, JaxbValidationsAnnotation annotation) {
        super(annotation);
        this.folderName = folderName;
        this.annotation = annotation;
    }

    @Override
    public String getFolderName() {
        return folderName;
    }

    @Override
    public String getNamespace() {
        return "a";
    }

    @Override
    public JaxbValidationsAnnotation getAnnotation() {
        return annotation;
    }

    protected String getAnnotationLibraryName() {
        return getAnnotation().name().toLowerCase();
    }

}
