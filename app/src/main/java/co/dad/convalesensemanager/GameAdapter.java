package co.dad.convalesensemanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dad.convalesensemanager.model.Exercise;

/**
 * Created by adrienviolet on 26/01/2017.
 */

public class GameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Exercise> exercises;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_game, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Exercise exercice = exercises.get(position);

        ((ExerciseViewHolder)holder).gameName.setText(exercice.getName());
        ((ExerciseViewHolder)holder).gameDescription.setText(exercice.getDescription());

        if (exercice.getName().equals("Arm Strength")) {
            ((ExerciseViewHolder)holder).icon.setImageDrawable(((ExerciseViewHolder)holder).icon.getResources().getDrawable(R.drawable.cloud_icon));
        } else if (exercice.getName().equals("Finger Strength")) {
            ((ExerciseViewHolder)holder).icon.setImageDrawable(((ExerciseViewHolder)holder).icon.getResources().getDrawable(R.drawable.rain_icon));
        } else if (exercice.getName().equals("Power Reflex")) {
            ((ExerciseViewHolder)holder).icon.setImageDrawable(((ExerciseViewHolder)holder).icon.getResources().getDrawable(R.drawable.balloon_icon));
        }

    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    @Override
    public long getItemId(int position) {
        return exercises.get(position).getId();
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.game_name)
        TextView gameName;

        @BindView(R.id.game_description)
        TextView gameDescription;

        @BindView(R.id.icon)
        ImageView icon;

        public ExerciseViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
