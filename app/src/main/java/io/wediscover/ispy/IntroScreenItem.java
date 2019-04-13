package io.wediscover.ispy;

public class IntroScreenItem {
    String introTitle, introDescription;
    int introScreenImage;

    public IntroScreenItem(String introTitle, String introDescription, int introScreenImage) {
        this.introTitle = introTitle;
        this.introDescription = introDescription;
        this.introScreenImage = introScreenImage;
    }

    public void setIntroTitle(String introTitle) {
        this.introTitle = introTitle;
    }

    public void setIntroDescription(String introDescription) {
        this.introDescription = introDescription;
    }

    public void setIntroScreenImage(int introScreenImage) {
        this.introScreenImage = introScreenImage;
    }

    public String getIntroTitle() {
        return introTitle;
    }

    public String getIntroDescription() {
        return introDescription;
    }

    public int getIntroScreenImage() {
        return introScreenImage;
    }

}
