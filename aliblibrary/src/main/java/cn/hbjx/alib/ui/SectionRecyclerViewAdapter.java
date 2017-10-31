package cn.hbjx.alib.ui;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by DengYiQian on 2017/6/6.
 */

public abstract class SectionRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TYPE_SECTION = 0;
    private static final int TYPE_CELL = 1;
    private ArrayList<Row> rows = new ArrayList();

    public SectionRecyclerViewAdapter() {
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            case 0:
                return this.onCreateViewHolderForSection(parent);
            case 1:
                return this.onCreateViewHolderForCell(parent);
            default:
                return null;
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(((SectionRecyclerViewAdapter.Row)this.rows.get(position)).isSection) {
            this.onBindViewHolderForSection(holder, (SectionRecyclerViewAdapter.Row)this.rows.get(position));
        } else {
            this.onBindViewHolderForCell(holder, (SectionRecyclerViewAdapter.Row)this.rows.get(position));
        }

    }

    public int getItemCount() {
        this.rows.clear();
        int totalSize = 0;
        int sections = this.getSections();

        for(int i = 0; i < sections; ++i) {
            this.rows.add(new SectionRecyclerViewAdapter.Row(i, -1, totalSize));
            ++totalSize;
            int items = this.getItemCount(i);

            for(int i1 = 0; i1 < items; ++i1) {
                this.rows.add(new SectionRecyclerViewAdapter.Row(i, i1, totalSize + i1));
            }

            totalSize += items;
        }

        return totalSize;
    }

    public int getItemViewType(int position) {
        return ((SectionRecyclerViewAdapter.Row)this.rows.get(position)).isSection?0:1;
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolderForCell(ViewGroup var1);

    public abstract RecyclerView.ViewHolder onCreateViewHolderForSection(ViewGroup var1);

    public abstract void onBindViewHolderForCell(RecyclerView.ViewHolder var1, SectionRecyclerViewAdapter.Row var2);

    public abstract void onBindViewHolderForSection(RecyclerView.ViewHolder var1, SectionRecyclerViewAdapter.Row var2);

    public abstract int getSections();

    public abstract int getItemCount(int var1);

    public class Row {
        public boolean isSection = false;
        public int section = 0;
        public int index = 0;
        public int position = 0;

        public Row(int sectionPosition, int cellPostion, int position) {
            this.section = sectionPosition;
            this.index = cellPostion;
            this.position = position;
            if(cellPostion == -1) {
                this.isSection = true;
            } else {
                this.isSection = false;
            }

        }
    }

}
