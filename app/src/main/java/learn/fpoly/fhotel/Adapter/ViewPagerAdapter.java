package learn.fpoly.fhotel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.constraintlayout.widget.ConstraintLayout;
import learn.fpoly.fhotel.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    // Mảng chứa các tài nguyên ảnh và văn bản
    int[] sliderAllImages = {
            R.drawable.bg,
            R.drawable.bg2,
            R.drawable.bg3,
    };
    int[] sliderAllTitle = {
            R.string.screen1,
            R.string.screen2,
            R.string.screen3,
    };
    int[] sliderAllDesc = {
            R.string.screen1desc,
            R.string.screen2desc,
            R.string.screen3desc,
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return sliderAllTitle.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_sceen, container, false);

        // Sử dụng ConstraintLayout làm nền
        ConstraintLayout sliderImage = view.findViewById(R.id.silderbackgroud);
        TextView sliderTitle = view.findViewById(R.id.slidertitle);
        TextView sliderDesc = view.findViewById(R.id.sliderdesc);

        // Đặt tài nguyên nền, tiêu đề và mô tả
        sliderImage.setBackgroundResource(sliderAllImages[position]);
        sliderTitle.setText(sliderAllTitle[position]);
        sliderDesc.setText(sliderAllDesc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
