package com.example.msdb.Entity;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "media", uniqueConstraints={@UniqueConstraint(columnNames = {"title" , "mediatype", "daterelease"})})
@Document(indexName = "media")
public class Media{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String mediatype;

    @Column(name="datecreate")
    private Date datecreate;
    @Column(name="dateedit")
    private Date dateedit;
    @Column(name="daterelease")
    private String daterelease;

    public Media() {
    }

    public Media(int id, String title, String mediatype, Date datecreate, Date dateedit, String daterelease) {
        this.id = id;
        this.title = title;
        this.mediatype = mediatype;
        this.datecreate = datecreate;
        this.dateedit = dateedit;
        this.daterelease = daterelease;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public Date getDatecreate() {
        return datecreate;
    }

    public void setDatecreate(Date datecreate) {
        this.datecreate = datecreate;
    }

    public Date getDateedit() {
        return dateedit;
    }

    public void setDateedit(Date dateedit) {
        this.dateedit = dateedit;
    }

    public String getDaterelease() {
        return daterelease;
    }

    public void setDaterelease(String daterelease) {
        this.daterelease = daterelease;
    }
}
