--
-- PostgreSQL database dump
--

-- Dumped from database version 11.9 (Debian 11.9-1.pgdg90+1)
-- Dumped by pg_dump version 11.9 (Debian 11.9-1.pgdg90+1)

--
-- Data for Name: u_sequences; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.u_sequences (s_id, s_nextnum) FROM stdin;
O_SEQ	0
INV_SEQ	0
OL_SEQ	0
C_SEQ	1
I_SEQ	24
\.


--
-- Data for Name: o_customer; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.o_customer (c_id, c_balance, c_contact, c_credit, c_credit_limit, c_first, c_last, c_since, c_version, c_ytd_payment, c_city, c_country, c_phone, c_state, c_street1, c_street2, c_zip) FROM stdin;
1	20000.00	contact	GC	1000.00	firstName	lastName	2020-08-27	1	100.00	city	county	phone	DE	street1	street2	zip
\.


--
-- Data for Name: o_customerinventory; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.o_customerinventory (ci_customerid, ci_id, ci_quantity, ci_value, ci_version, ci_itemid) FROM stdin;
\.


--
-- Data for Name: o_item; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.o_item (i_id, i_category, i_desc, i_discount, i_name, i_price, i_version) FROM stdin;
1	1	description1	0.0000	name1	100.00	1
2	1	description2	0.0000	name2	100.00	1
3	2	description3	10.0000	name3	200.00	1
4	2	Assembly 1 which is build from 3 parts	10.0000	Assembly 1	200.00	1
5	2	Assembly 2 which is build from 2 parts	10.0000	Assembly 2	200.00	1
6	2	Assembly 3 which is build from 2 parts	10.0000	Assembly 3	200.00	1
7	2	Assembly 4 which is build from 2 parts	10.0000	Assembly 4	200.00	1
8	2	Assembly 5 which is build from 2 parts	10.0000	Assembly 5	200.00	1
9	2	Assembly 6 which is build from 2 parts	10.0000	Assembly 6	200.00	1
10	2	Assembly 7 which is build from 2 parts	10.0000	Assembly 7	200.00	1
11	2	Assembly 8 which is build from 2 parts	10.0000	Assembly 8	200.00	1
12	2	Assembly 9 which is build from 2 parts	10.0000	Assembly 9	200.00	1
13	2	Assembly 10 which is build from 2 parts	10.0000	Assembly 10	200.00	1
14	2	Assembly 11 which is build from 2 parts	10.0000	Assembly 11	200.00	1
15	2	Assembly 12 which is build from 2 parts	10.0000	Assembly 12	200.00	1
16	2	Assembly 13 which is build from 2 parts	10.0000	Assembly 13	200.00	1
17	2	Assembly 14 which is build from 2 parts	10.0000	Assembly 14	200.00	1
18	2	Assembly 15 which is build from 2 parts	10.0000	Assembly 15	200.00	1
19	2	Assembly 16 which is build from 2 parts	10.0000	Assembly 16	200.00	1
20	2	Assembly 17 which is build from 2 parts	10.0000	Assembly 17	200.00	1
21	2	Assembly 18 which is build from 2 parts	10.0000	Assembly 18	200.00	1
22	2	Assembly 19 which is build from 2 parts	10.0000	Assembly 19	200.00	1
23	2	Assembly 20 which is build from 2 parts	10.0000	Assembly 20	200.00	1
24	2	Assembly 21 which is build from 2 parts	10.0000	Assembly 21	200.00	1
\.


--
-- Data for Name: o_orderline; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.o_orderline (ol_o_id, ol_id, ol_msrp, ol_qty, ol_ship_date, ol_status, ol_total_value, ol_version, ol_i_id) FROM stdin;
\.


--
-- Data for Name: o_orders; Type: TABLE DATA; Schema: public; Owner: order_user
--

COPY public.o_orders (o_id, o_discount, o_entry_date, o_ol_cnt, o_ship_date, o_status, o_total, o_version, o_c_id) FROM stdin;
\.




--
-- PostgreSQL database dump complete
--

