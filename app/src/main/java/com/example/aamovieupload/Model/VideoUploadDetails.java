package com.example.aamovieupload.Model;

public class VideoUploadDetails {
    private String videoSlide, videoType, videoThumb,
            videoUrl, videoTitle, videoDescription, videoCategory;

    public VideoUploadDetails() {
    }

    public VideoUploadDetails(String videoSlide, String videoType, String videoThumb, String videoUrl, String videoTitle, String videoDescription, String videoCategory) {
        this.videoSlide = videoSlide;
        this.videoType = videoType;
        this.videoThumb = videoThumb;
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoCategory = videoCategory;
    }

    public String getVideoSlide() {
        return videoSlide;
    }

    public void setVideoSlide(String videoSlide) {
        this.videoSlide = videoSlide;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }
}
