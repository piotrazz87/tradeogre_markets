create table market(
	id serial not null
		constraint market_pkey
			primary key,
	base_currency varchar,
	target_currency varchar,
	created_date timestamp,
	current_price numeric(10,8),
	volume numeric(10,8),
	start_price numeric(10,8),
	low numeric(10,8),
	high numeric(10,8),
	buy_offer numeric(10,8),
	sell_offer numeric(10,8)
);