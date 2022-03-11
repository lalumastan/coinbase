package icsdiscover.cb;

import java.util.ArrayList;
import java.util.Date;

record Pagination(String ending_before, String starting_after, String previous_ending_before, String next_starting_after, int limit, String order, String previous_uri, String next_uri) {}

record Balance(String amount, String currency) {}

record Data(String id, String name, boolean primary, String type, String currency, Balance balance, Date created_at, Date updated_at, String resource, String resource_path, boolean allow_deposits, boolean allow_withdrawals, Balance native_balance) {}

public record CBAccount(Pagination pagination, ArrayList<Data> data) {}
