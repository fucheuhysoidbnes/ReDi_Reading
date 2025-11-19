package com.example.redi.admin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.redi.R;
import com.example.redi.admin.adapters.AdminOrderAdapter;
import com.example.redi.common.models.Order;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;

import java.util.*;

public class OrdersFragment extends Fragment implements AdminOrderAdapter.OnActionListener {

    private RecyclerView recyclerView;
    private AdminOrderAdapter adapter;
    private OrderRepository repository;
    private ProgressBar progressBar;
    private EditText etSearch;
    private Spinner spStatusFilter, spDay, spMonth, spYear;

    private List<Order> lastLoaded = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new OrderRepository();

        recyclerView = v.findViewById(R.id.rvOrders);
        progressBar = v.findViewById(R.id.progressOrders);
        etSearch = v.findViewById(R.id.etOrderSearch);
        spStatusFilter = v.findViewById(R.id.spStatusFilter);
        spDay = v.findViewById(R.id.spDay);
        spMonth = v.findViewById(R.id.spMonth);
        spYear = v.findViewById(R.id.spYear);

        adapter = new AdminOrderAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList("Trạng thái", com.example.redi.common.utils.Constants.STATUS_PENDING,
                        com.example.redi.common.utils.Constants.STATUS_DELIVERING,
                        com.example.redi.common.utils.Constants.STATUS_RECEIVED,
                        com.example.redi.common.utils.Constants.STATUS_CANCELLED));
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusFilter.setAdapter(statusAdapter);

        // day / month / year spinners
        List<String> days = new ArrayList<>();
        days.add("Ngày");
        for (int i = 1; i <= 31; i++) days.add(String.format(Locale.getDefault(), "%02d", i));
        spDay.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days));

        List<String> months = new ArrayList<>();
        months.add("Tháng");
        for (int i = 1; i <= 12; i++) months.add(String.format(Locale.getDefault(), "%02d", i));
        spMonth.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months));

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        years.add("Năm");
        for (int y = thisYear; y >= thisYear - 5; y--) years.add(String.valueOf(y));
        spYear.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years));

        AdapterView.OnItemSelectedListener filterChanged = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { applyLocalFilters(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spStatusFilter.setOnItemSelectedListener(filterChanged);
        spDay.setOnItemSelectedListener(filterChanged);
        spMonth.setOnItemSelectedListener(filterChanged);
        spYear.setOnItemSelectedListener(filterChanged);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { adapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadOrders();
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        // load all orders for admin
        repository.listenAllOrders(new DataSourceCallback<List<Order>>() {
            @Override public void onSuccess(List<Order> result) {
                progressBar.setVisibility(View.GONE);
                lastLoaded = result != null ? result : new ArrayList<>();
                applyLocalFilters();
            }
            @Override public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyLocalFilters() {
        String status = spStatusFilter.getSelectedItem().toString();
        String day = spDay.getSelectedItem().toString();
        String month = spMonth.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();

        List<Order> filtered = new ArrayList<>();
        for (Order o : lastLoaded) {
            if (!"Trạng thái".equals(status) && (o.getStatus() == null || !o.getStatus().equals(status))) continue;
            if (o.getDateOrder() == null) continue;
            // date format expected yyyy-MM-dd
            String[] parts = o.getDateOrder().split("-");
            if (parts.length < 3) continue;
            String yy = parts[0], mm = parts[1], dd = parts[2];

            if (!"Ngày".equals(day) && !dd.equals(day)) continue;
            if (!"Tháng".equals(month) && !mm.equals(month)) continue;
            if (!"Năm".equals(year) && !yy.equals(year)) continue;

            filtered.add(o);
        }
        adapter.updateList(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        repository.removeListeners();
    }

    @Override
    public void onViewOrder(Order order) {
        OrderDetailFragment detail = OrderDetailFragment.newInstance(order.getOrderId());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.fade_out,
                        android.R.anim.slide_in_left, android.R.anim.fade_out)
                .replace(R.id.admin_fragment_container, detail)
                .addToBackStack(null)
                .commit();
    }
}
