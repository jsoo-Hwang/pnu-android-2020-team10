package mapactivity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.jakshim.R;

import java.util.ArrayList;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder>{
    private  ArrayList<PlaceData> mData = null ;

    private ItemClick itemClick;

    public interface ItemClick{
        public void onClick(View view, int position);
    }

    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }



    public void clear()
    {
        mData =  new ArrayList<>();
    }

    public void add(PlaceData a)
    {
        mData.add(a);
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName ;
        TextView placeAdd;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            placeName = itemView.findViewById(R.id.placeName) ;
            placeAdd = itemView.findViewById(R.id.placeAdd);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    PlaceListAdapter(ArrayList<PlaceData> list) {
        mData = list ;
    }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PlaceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.placelist_item, parent, false) ;
        PlaceListAdapter.ViewHolder vh = new PlaceListAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PlaceListAdapter.ViewHolder holder, int position) {
        PlaceData place = mData.get(position) ;

        Log.d("adapter_place", place.getName() + "\n" + place.getAdd());

        holder.placeName.setText(place.getName()) ;
        holder.placeAdd.setText(place.getAdd());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v,position);
                }
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}
