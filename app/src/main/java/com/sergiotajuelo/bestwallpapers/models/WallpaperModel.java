package com.sergiotajuelo.bestwallpapers.models;

import java.io.Serializable;

public class WallpaperModel implements Serializable {
    private String id, originalUrl, mediumUrl, caption, creator, creatorUrl, width, height,
            profileImageSmall, profileImageLarge, bio, instagram_username, twitter_username,
            total_photos, username;

    private boolean usuarioActual = false;

    public WallpaperModel() {}

    public WallpaperModel(String id, String originalUrl, String mediumUrl, String caption, String creator,
                          String creatorUrl, String width, String height, String profileImageSmall,
                          String profileImageLarge, String bio, String instagram_username,
                          String twitter_username, String total_photos, String username, boolean usuarioActual) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.mediumUrl = mediumUrl;
        this.caption = caption;
        this.creator = creator;
        this.creatorUrl = creatorUrl;
        this.height = height;
        this.width = width;
        this.profileImageLarge = profileImageLarge;
        this.profileImageSmall = profileImageSmall;
        this.bio = bio;
        this.instagram_username = instagram_username;
        this.total_photos = total_photos;
        this.twitter_username = twitter_username;
        this.username = username;
        this.usuarioActual = usuarioActual;

        if(bio.equals("") || bio.equals("null"))
            this.bio = "Sin biografía.";
    }

    public WallpaperModel(String id, String creator, String mediumUrl, String originalUrl, boolean usuarioActual,
                          String bio, String profileImageSmall, String profileImageLarge) {
        this.id = id;
        this.creator = creator;
        this.mediumUrl = mediumUrl;
        this.originalUrl = originalUrl;
        this.usuarioActual = usuarioActual;
        this.bio = bio;
        this.profileImageLarge = profileImageLarge;
        this.profileImageSmall = profileImageSmall;

        if(bio.equals("") || bio.equals("null"))
            this.bio = "Sin biografía.";
    }

    public WallpaperModel(String id, String creator, String bio, String total_photos, boolean usuarioActual,
                          String profileImageSmall, String profileImageLarge) {
        this.id = id;
        this.creator = creator;
        this.bio = bio;
        this.total_photos = total_photos;
        this.usuarioActual = usuarioActual;
        this.profileImageLarge = profileImageLarge;
        this.profileImageSmall = profileImageSmall;

        if(bio.equals("") || bio.equals("null"))
            this.bio = "Sin biografía.";
    }

    public WallpaperModel(String id, String bio, String username, String creator, String profileImageLarge) {
        this.id = id;
        this.creator = creator;
        this.bio = bio;
        this.username = username;
        this.profileImageLarge = profileImageLarge;

        if(bio.equals("") || bio.equals("null"))
            this.bio = "Sin biografía.";
    }

    public boolean isUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(boolean usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorUrl() {
        return creatorUrl;
    }

    public void setCreatorUrl(String creatorUrl) {
        this.creatorUrl = creatorUrl;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getProfileImageSmall() {
        return profileImageSmall;
    }

    public void setProfileImageSmall(String profileImageSmall) {
        this.profileImageSmall = profileImageSmall;
    }

    public String getProfileImageLarge() {
        return profileImageLarge;
    }

    public void setProfileImageLarge(String profileImageLarge) {
        this.profileImageLarge = profileImageLarge;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInstagram_username() {
        return instagram_username;
    }

    public void setInstagram_username(String instagram_username) {
        this.instagram_username = instagram_username;
    }

    public String getTwitter_username() {
        return twitter_username;
    }

    public void setTwitter_username(String twitter_username) {
        this.twitter_username = twitter_username;
    }

    public String getTotal_photos() {
        return total_photos;
    }

    public void setTotal_photos(String total_photos) {
        this.total_photos = total_photos;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
