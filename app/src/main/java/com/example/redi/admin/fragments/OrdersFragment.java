package com.example.redi.admin.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redi.R;
import com.example.redi.admin.adapters.AdminOrderAdapter;
import com.example.redi.common.models.Order;
import com.example.redi.common.models.User;
import com.example.redi.data.DataSourceCallback;
import com.example.redi.data.repository.OrderRepository;
import com.example.redi.data.repository.UserRepository;

import java.util.*;

public class OrdersFragment extends Fragment implements AdminOrderAdapter.OnActionListener {

    private RecyclerView recyclerView;
    private AdminOrderAdapter adapter;
    private OrderRepository orderRepo;
    private UserRepository userRepo;

    private ProgressBar progressBar;
    private EditText etSearch;
    private Spinner spStatusFilter, spDay, spMonth, spYear;

    private List<Order> lastLoaded = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        orderRepo = new OrderRepository();
        userRepo = new UserRepository();

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

        setupSpinners();
        setupSearch();

        loadUsersThenOrders();
    }

    // ============================================================
    // LOAD USERS FIRST (for search by email)
    // ============================================================
    private void loadUsersThenOrders() {
        progressBar.setVisibility(View.VISIBLE);

        userRepo.getAllUsers(new DataSourceCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                userMap.clear();
                for (User u : result) {
                    if (u.getId() != null) {
                        userMap.put(u.getId(), u);
                    }
                }
                loadOrders();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Lỗi tải user: " + errorMessage, Toast.LENGTH_SHORT).show();
                loadOrders();
            }
        });
    }

    private void loadOrders() {
        orderRepo.listenAllOrders(new DataSourceCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> result) {
                progressBar.setVisibility(View.GONE);
                lastLoaded = result != null ? result : new ArrayList<>();
                applyFilters();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi tải đơn: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ============================================================
    // SPINNER SETUP
    // ============================================================
    private void setupSpinners() {

        // Status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList(
                        "Trạng thái",
                        com.example.redi.common.utils.Constants.STATUS_PENDING,
                        com.example.redi.common.utils.Constants.STATUS_DELIVERING,
                        com.example.redi.common.utils.Constants.STATUS_RECEIVED,
                        com.example.redi.common.utils.Constants.STATUS_CANCELLED
                )
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatusFilter.setAdapter(statusAdapter);

        // day spinner
        List<String> days = new ArrayList<>();
        days.add("Ngày");
        for (int i = 1; i <= 31; i++)
            days.add(String.format(Locale.getDefault(), "%02d", i));
        spDay.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, days));

        // month spinner
        List<String> months = new ArrayList<>();
        months.add("Tháng");
        for (int i = 1; i <= 12; i++)
            months.add(String.format(Locale.getDefault(), "%02d", i));
        spMonth.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months));

        // year spinner
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        years.add("Năm");
        for (int y = thisYear; y >= thisYear - 5; y--)
            years.add(String.valueOf(y));
        spYear.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years));

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spStatusFilter.setOnItemSelectedListener(listener);
        spDay.setOnItemSelectedListener(listener);
        spMonth.setOnItemSelectedListener(listener);
        spYear.setOnItemSelectedListener(listener);
    }

    // ============================================================
    // SEARCH
    // ============================================================
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private String safeSpinner(Spinner sp, String def) {
        Object o = sp.getSelectedItem();
        if (o == null) return def;
        return o.toString();
    }

    // ============================================================
    // FILTER LOGIC (SAFE, NO MORE CRASH)
    // ============================================================
    private void applyFilters() {

        if (spStatusFilter == null) return;

        String status = safeSpinner(spStatusFilter, "Trạng thái");
        String day    = safeSpinner(spDay, "Ngày");
        String month  = safeSpinner(spMonth, "Tháng");
        String year   = safeSpinner(spYear, "Năm");

        String searchEmail = etSearch.getText().toString().trim().toLowerCase();

        List<Order> filtered = new ArrayList<>();

        for (Order o : lastLoaded) {

            // --- search by email ---
            User u = userMap.get(o.getUserId());
            String email = (u != null && u.getEmail() != null)
                    ? u.getEmail().toLowerCase()
                    : "";

            if (!searchEmail.isEmpty() && !email.contains(searchEmail))
                continue;

            // --- filter status ---
            if (!"Trạng thái".equals(status) && !status.equals(o.getStatus()))
                continue;

            if (o.getDateOrder() == null) continue;

            // date format: yyyy-MM-dd
            String[] p = o.getDateOrder().split("-");
            if (p.length < 3) continue;

            String yy = p[0];
            String mm = p[1];
            String dd = p[2];

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
        orderRepo.removeListeners();
    }

    // ============================================================
    // OPEN ORDER DETAIL
    // ============================================================
    @Override
    public void onViewOrder(Order order) {
        OrderDetailFragment detail = OrderDetailFragment.newInstance(order.getOrderId());

        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left, android.R.anim.fade_out,
                        android.R.anim.slide_in_left, android.R.anim.fade_out)
                .replace(R.id.admin_fragment_container, detail)
                .addToBackStack(null)
                .commit();
    }
}
