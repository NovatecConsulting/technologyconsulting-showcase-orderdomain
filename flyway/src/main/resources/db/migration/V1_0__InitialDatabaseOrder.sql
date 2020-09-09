--
-- PostgreSQL database dump
--

-- Dumped from database version 11.9 (Debian 11.9-1.pgdg90+1)
-- Dumped by pg_dump version 11.9 (Debian 11.9-1.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: o_customer; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.o_customer (
    c_id integer NOT NULL,
    c_balance numeric(12,2),
    c_contact character varying(25),
    c_credit character varying(2),
    c_credit_limit numeric(12,2),
    c_first character varying(16),
    c_last character varying(16),
    c_since date,
    c_version integer,
    c_ytd_payment numeric(12,2),
    c_city character varying(20),
    c_country character varying(10),
    c_phone character varying(16),
    c_state character varying(2),
    c_street1 character varying(20),
    c_street2 character varying(20),
    c_zip character varying(9)
);


ALTER TABLE public.o_customer OWNER TO order_user;

--
-- Name: o_customerinventory; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.o_customerinventory (
    ci_customerid integer NOT NULL,
    ci_id integer NOT NULL,
    ci_quantity integer,
    ci_value numeric(12,2),
    ci_version integer,
    ci_itemid character varying(20)
);


ALTER TABLE public.o_customerinventory OWNER TO order_user;

--
-- Name: o_item; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.o_item (
    i_id character varying(20) NOT NULL,
    i_category integer NOT NULL,
    i_desc character varying(100),
    i_discount numeric(6,4),
    i_name character varying(35),
    i_price numeric(12,2),
    i_version integer
);


ALTER TABLE public.o_item OWNER TO order_user;

--
-- Name: o_orderline; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.o_orderline (
    ol_o_id integer NOT NULL,
    ol_id integer NOT NULL,
    ol_msrp numeric(12,2),
    ol_qty integer,
    ol_ship_date date,
    ol_status integer,
    ol_total_value numeric(12,2),
    ol_version integer,
    ol_i_id character varying(20)
);


ALTER TABLE public.o_orderline OWNER TO order_user;

--
-- Name: o_orders; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.o_orders (
    o_id integer NOT NULL,
    o_discount numeric(12,2),
    o_entry_date timestamp without time zone,
    o_ol_cnt integer,
    o_ship_date date,
    o_status integer,
    o_total numeric(12,2),
    o_version integer,
    o_c_id integer
);


ALTER TABLE public.o_orders OWNER TO order_user;

--
-- Name: u_sequences; Type: TABLE; Schema: public; Owner: order_user
--

CREATE TABLE public.u_sequences (
    s_id character varying(50) NOT NULL,
    s_nextnum numeric(38,0)
);


ALTER TABLE public.u_sequences OWNER TO order_user;



--
-- Name: o_customer o_customer_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_customer
    ADD CONSTRAINT o_customer_pkey PRIMARY KEY (c_id);


--
-- Name: o_customerinventory o_customerinventory_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_customerinventory
    ADD CONSTRAINT o_customerinventory_pkey PRIMARY KEY (ci_customerid, ci_id);


--
-- Name: o_item o_item_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_item
    ADD CONSTRAINT o_item_pkey PRIMARY KEY (i_id);


--
-- Name: o_orderline o_orderline_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_orderline
    ADD CONSTRAINT o_orderline_pkey PRIMARY KEY (ol_o_id, ol_id);


--
-- Name: o_orders o_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_orders
    ADD CONSTRAINT o_orders_pkey PRIMARY KEY (o_id);


--
-- Name: u_sequences u_sequences_pkey; Type: CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.u_sequences
    ADD CONSTRAINT u_sequences_pkey PRIMARY KEY (s_id);


--
-- Name: o_customerinventory fk_o_customerinventory_ci_customerid; Type: FK CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_customerinventory
    ADD CONSTRAINT fk_o_customerinventory_ci_customerid FOREIGN KEY (ci_customerid) REFERENCES public.o_customer(c_id);


--
-- Name: o_orderline fk_o_orderline_ol_o_id; Type: FK CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_orderline
    ADD CONSTRAINT fk_o_orderline_ol_o_id FOREIGN KEY (ol_o_id) REFERENCES public.o_orders(o_id);


--
-- Name: o_orders fk_o_orders_o_c_id; Type: FK CONSTRAINT; Schema: public; Owner: order_user
--

ALTER TABLE ONLY public.o_orders
    ADD CONSTRAINT fk_o_orders_o_c_id FOREIGN KEY (o_c_id) REFERENCES public.o_customer(c_id);


--
-- PostgreSQL database dump complete
--