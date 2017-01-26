package co.dad.convalesensemanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dad.convalesensemanager.model.Plan;

/**
 * Created by adrienviolet on 26/01/2017.
 */

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Plan> plans;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Plan plan = plans.get(position);

        ((PlanViewHolder)holder).patientName.setText(plan.getPatient().getUsername());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy");
            ((PlanViewHolder)holder).startDate.setText(sdf.format(plan.getStart()));
            ((PlanViewHolder)holder).endDate.setText(sdf.format(plan.getEnd()));

        } catch (Exception e) {

        }

        ((PlanViewHolder)holder).numberOfGames.setText(String.valueOf(plan.getExercises().size()));


    }

    @Override
    public int getItemCount() {
        return plans == null ? 0 : plans.size();
    }

    @Override
    public long getItemId(int position) {
        return plans.get(position).getId();
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.start_date)
        TextView startDate;

        @BindView(R.id.end_date)
        TextView endDate;

        @BindView(R.id.name)
        TextView patientName;

        @BindView(R.id.description)
        TextView description;

        @BindView(R.id.goals)
        TextView goalsOfTheDay;

        @BindView(R.id.session_per_day)
        TextView sessionPerDay;

        @BindView(R.id.number_game)
        TextView numberOfGames;

        public PlanViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
