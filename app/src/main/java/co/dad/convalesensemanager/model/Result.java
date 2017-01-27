package co.dad.convalesensemanager.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by adrienviolet on 27/01/2017.
 */

public class Result {

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("start")
    @Expose
    private Date startTime;

    @SerializedName("end")
    @Expose
    private Date endTime;

    @SerializedName("exercise_id")
    @Expose
    private int exerciceId;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getExerciceId() {
        return exerciceId;
    }

    public void setExerciceId(int exerciceId) {
        this.exerciceId = exerciceId;
    }
}
