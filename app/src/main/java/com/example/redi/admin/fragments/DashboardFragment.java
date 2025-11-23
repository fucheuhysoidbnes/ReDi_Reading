package com.example.redi.admin.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.admin.adapters.DashboardTopProductAdapter;
import com.example.redi.common.models.CartItem;
import com.example.redi.common.models.Order;
import com.example.redi.common.utils.Constants;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.data.*;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardFragment extends Fragment {

    private TextView tvTotalOrders, tvPendingOrders, tvDeliveringOrders, tvDeliveredOrders, tvRevenue;
    private PieChart pieChartStatus;
    private BarChart barChartRevenue;
    private LineChart lineChartRevenue;
    private RecyclerView rvTopProducts;

    private Spinner spFilterType;
    private LinearLayout layoutDateRange;
    private TextView tvFromDate, tvToDate;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private OrderRepository orderRepository;
    private List<Order> allOrders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        orderRepository = new OrderRepository();

        tvTotalOrders = v.findViewById(R.id.tvTotalOrders);
        tvPendingOrders = v.findViewById(R.id.tvPendingOrders);
        tvDeliveringOrders = v.findViewById(R.id.tvDeliveringOrders); // NEW
        tvDeliveredOrders = v.findViewById(R.id.tvDeliveredOrders);
        tvRevenue = v.findViewById(R.id.tvRevenue);

        pieChartStatus = v.findViewById(R.id.pieChartStatus);
        barChartRevenue = v.findViewById(R.id.barChartRevenue);
        lineChartRevenue = v.findViewById(R.id.lineChartRevenue);

        rvTopProducts = v.findViewById(R.id.rvTopProducts);
        rvTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        spFilterType = v.findViewById(R.id.spFilterType);
        layoutDateRange = v.findViewById(R.id.layoutDateRange);
        tvFromDate = v.findViewById(R.id.tvFromDate);
        tvToDate = v.findViewById(R.id.tvToDate);

        setupDatePickers();
        setupFilterDropdown();
        loadOrders();
    }

    private void setupDatePickers() {
        tvFromDate.setOnClickListener(v -> pickDate(tvFromDate));
        tvToDate.setOnClickListener(v -> pickDate(tvToDate));
    }

    private void pickDate(TextView target) {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(
                getContext(),
                (dp, y, m, d) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(y, m, d);
                    target.setText(sdf.format(cal.getTime()));
                    updateChartsByFilter();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void setupFilterDropdown() {
        spFilterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String type = parent.getItemAtPosition(pos).toString();

                switch (type) {
                    case "Hôm nay":
                        layoutDateRange.setVisibility(View.GONE);
                        setTodayRange();
                        break;

                    case "Theo tháng":
                        layoutDateRange.setVisibility(View.GONE);
                        setCurrentMonthRange();
                        break;

                    case "Theo năm":
                        layoutDateRange.setVisibility(View.GONE);
                        setCurrentYearRange();
                        break;

                    case "Khoảng thời gian":
                        layoutDateRange.setVisibility(View.VISIBLE);
                        break;
                }
                updateChartsByFilter();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setTodayRange() {
        String today = sdf.format(new Date());
        tvFromDate.setText(today);
        tvToDate.setText(today);
    }

    private void setCurrentMonthRange() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);

        tvFromDate.setText(sdf.format(c.getTime()));
        tvToDate.setText(sdf.format(new Date()));
    }

    private void setCurrentYearRange() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        tvFromDate.setText(year + "-01-01");
        tvToDate.setText(sdf.format(new Date()));
    }

    private void loadOrders() {
        orderRepository.listenAllOrders(new DataSourceCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> result) {
                allOrders = result != null ? result : new ArrayList<>();
                updateOverviewCards();
                updateChartsByFilter();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOverviewCards() {
        int total = allOrders.size();
        int pending = 0;
        int delivering = 0;
        int delivered = 0;

        long todayRevenue = 0;
        String today = sdf.format(new Date());

        for (Order o : allOrders) {

            if (Constants.STATUS_PENDING.equals(o.getStatus())) pending++;
            if (Constants.STATUS_DELIVERING.equals(o.getStatus())) delivering++;
            if (Constants.STATUS_RECEIVED.equals(o.getStatus())) delivered++;

            if (o.getPayment() != null &&
                    Constants.PAYMENT_PAID.equals(o.getPayment().getStatus()) &&
                    today.equals(o.getDateOrder())) {

                todayRevenue += o.getPayment().getAmount();
            }
        }

        tvTotalOrders.setText(String.valueOf(total));
        tvPendingOrders.setText(String.valueOf(pending));
        tvDeliveringOrders.setText(String.valueOf(delivering)); // NEW
        tvDeliveredOrders.setText(String.valueOf(delivered));
        tvRevenue.setText(NumberFormat.getInstance().format(todayRevenue) + "đ");
    }

    private List<Order> filterByRange() {
        List<Order> list = new ArrayList<>();

        try {
            Date from = sdf.parse(tvFromDate.getText().toString());
            Date to = sdf.parse(tvToDate.getText().toString());

            for (Order o : allOrders) {
                if (o.getDateOrder() == null) continue;

                Date d = sdf.parse(o.getDateOrder());
                if (d != null && !d.before(from) && !d.after(to)) {
                    list.add(o);
                }
            }
        } catch (Exception ignored) {}

        return list;
    }

    private List<Order> getFilteredOrders() {
        return filterByRange();
    }

    private void updateChartsByFilter() {
        List<Order> filtered = getFilteredOrders();

        updatePieChart(filtered);
        updateBarChart(filtered);
        updateLineChart(filtered);
        updateTopProducts(filtered);
    }

    // ==========================================================
    // PIE CHART
    // ==========================================================
    private void updatePieChart(List<Order> data) {
        pieChartStatus.clear();

        int pending = 0, delivering = 0, received = 0, cancelled = 0;

        for (Order o : data) {
            if (o.getStatus() == null) continue;

            switch (o.getStatus()) {
                case Constants.STATUS_PENDING: pending++; break;
                case Constants.STATUS_DELIVERING: delivering++; break;
                case Constants.STATUS_RECEIVED: received++; break;
                case Constants.STATUS_CANCELLED: cancelled++; break;
            }
        }

        List<PieEntry> entries = new ArrayList<>();

        if (pending + delivering + received + cancelled == 0) {
            entries.add(new PieEntry(1, "Không có dữ liệu"));
        } else {
            if (pending > 0) entries.add(new PieEntry(pending, "Chờ"));
            if (delivering > 0) entries.add(new PieEntry(delivering, "Giao"));
            if (received > 0) entries.add(new PieEntry(received, "Hoàn tất"));
            if (cancelled > 0) entries.add(new PieEntry(cancelled, "Hủy"));
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(
                getResources().getColor(R.color.status_pending),
                getResources().getColor(R.color.status_delivering),
                getResources().getColor(R.color.status_received),
                getResources().getColor(R.color.status_cancelled)
        );

        PieData pd = new PieData(ds);
        pd.setValueTextSize(12f);

        pieChartStatus.setData(pd);
        pieChartStatus.getDescription().setEnabled(false);
        pieChartStatus.invalidate();
    }

    // ==========================================================
    // BAR CHART
    // ==========================================================
    private void updateBarChart(List<Order> data) {
        barChartRevenue.clear();

        TreeMap<String, Long> map = new TreeMap<>();

        for (Order o : data) {

            //Chỉ tính doanh thu của đơn ĐÃ NHẬN
            if (!Constants.STATUS_RECEIVED.equals(o.getStatus()))
                continue;

            if (o.getPayment() == null ||
                    !Constants.PAYMENT_PAID.equals(o.getPayment().getStatus()))
                continue;

            map.put(o.getDateOrder(),
                    map.getOrDefault(o.getDateOrder(), 0L) + o.getPayment().getAmount());
        }

        List<BarEntry> entries = new ArrayList<>();
        int idx = 0;

        if (map.isEmpty()) {
            entries.add(new BarEntry(0, 0));
        } else {
            for (Long val : map.values())
                entries.add(new BarEntry(idx++, val));
        }

        BarDataSet ds = new BarDataSet(entries, "Doanh thu");
        ds.setColor(getResources().getColor(R.color.status_delivering));

        barChartRevenue.setData(new BarData(ds));
        barChartRevenue.getDescription().setEnabled(false);
        barChartRevenue.invalidate();
    }

    // ==========================================================
    // LINE CHART
    // ==========================================================
    private void updateLineChart(List<Order> data) {
        lineChartRevenue.clear();

        TreeMap<String, Long> map = new TreeMap<>();

        for (Order o : data) {

            // Chỉ tính doanh thu đơn ĐÃ NHẬN HÀNG
            if (!Constants.STATUS_RECEIVED.equals(o.getStatus()))
                continue;

            if (o.getPayment() == null ||
                    !Constants.PAYMENT_PAID.equals(o.getPayment().getStatus()))
                continue;

            map.put(o.getDateOrder(),
                    map.getOrDefault(o.getDateOrder(), 0L) + o.getPayment().getAmount());
        }

        List<Entry> entries = new ArrayList<>();
        int idx = 0;

        if (map.isEmpty()) {
            entries.add(new Entry(0, 0));
        } else {
            for (Long val : map.values())
                entries.add(new Entry(idx++, val));
        }

        LineDataSet ds = new LineDataSet(entries, "Doanh thu");
        ds.setColor(getResources().getColor(R.color.status_delivering));
        ds.setCircleColor(getResources().getColor(R.color.status_delivering));
        ds.setCircleRadius(4f);
        ds.setLineWidth(2f);

        lineChartRevenue.setData(new LineData(ds));
        lineChartRevenue.getDescription().setEnabled(false);
        lineChartRevenue.invalidate();
    }


    // ==========================================================
    // TOP PRODUCTS
    // ==========================================================
    private void updateTopProducts(List<Order> data) {

        Map<String, CartItem> map = new HashMap<>();

        for (Order o : data) {
            if (!Constants.STATUS_RECEIVED.equals(o.getStatus())) continue;
            if (o.getBooklist() == null) continue;

            for (CartItem item : o.getBooklist().values()) {

                if (!map.containsKey(item.getBook_id())) {
                    map.put(item.getBook_id(),
                            new CartItem(
                                    item.getBook_id(),
                                    item.getTitle(),
                                    item.getImageUrl(),
                                    item.getPrice(),
                                    item.getQty()
                            ));
                } else {
                    map.get(item.getBook_id())
                            .setQty(map.get(item.getBook_id()).getQty() + item.getQty());
                }
            }
        }

        List<CartItem> list = new ArrayList<>(map.values());
        list.sort((a, b) -> b.getQty() - a.getQty());

        rvTopProducts.setAdapter(new DashboardTopProductAdapter(list));
    }
}
