package com.example.calendardemo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.calendardemo.R;
import com.example.calendardemo.databinding.RvItemBinding;

public class CalendarTest2Activity extends AppCompatActivity {

    private CalendarTest2Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar2);

        initRecyclerView();

    }

    private void initRecyclerView() {
        mBinding.test2Recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mBinding.test2Recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mBinding.test2Recyclerview.setAdapter(new TestAdapter());
    }

    static class TestAdapter extends RecyclerView.Adapter<TestAdapter.Holder> {
        class Holder extends RecyclerView.ViewHolder {

            private final RvItemBinding mBinding;

            public Holder(@NonNull RvItemBinding binding) {
                super(binding.getRoot());
                this.mBinding = binding;
            }
        }

        @NonNull
        @Override
        public TestAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RvItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rv_item, parent, false);
            return new Holder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.mBinding.tvContent.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }
}
