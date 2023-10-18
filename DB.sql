PGDMP                     	    {           wizard    15.3    15.3                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16877    wizard    DATABASE     h   CREATE DATABASE wizard WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'C';
    DROP DATABASE wizard;
                postgres    false            H           1247    17010    human_language    TYPE       CREATE TYPE public.human_language AS ENUM (
    'ENGLISH',
    'CHINESE',
    'SPANISH',
    'HINDI',
    'ARABIC',
    'FRENCH',
    'RUSSIAN',
    'PORTUGUESE',
    'BENGALI',
    'GERMAN',
    'JAPANESE',
    'PUNJABI',
    'KOREAN',
    'TELUGU',
    'MARATHI'
);
 !   DROP TYPE public.human_language;
       public          postgres    false            �            1259    17106    users    TABLE     �  CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(30) NOT NULL,
    age integer NOT NULL,
    email character varying(30) NOT NULL,
    password character varying(100) NOT NULL,
    role character varying(10),
    message character varying(500),
    avatar_title character varying,
    activation_code character varying(255),
    is_in_recovering boolean,
    resume_pass_key character varying,
    CONSTRAINT person_age_check CHECK ((age > 17))
);
    DROP TABLE public.users;
       public         heap    postgres    false            �            1259    17105    person_id_seq    SEQUENCE     �   ALTER TABLE public.users ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public          postgres    false    215            �            1259    17131    resumes    TABLE     �   CREATE TABLE public.resumes (
    id integer NOT NULL,
    title character varying(200) NOT NULL,
    path character varying(200) NOT NULL,
    owner_id integer,
    language character varying
);
    DROP TABLE public.resumes;
       public         heap    postgres    false            �            1259    17130    resume_id_seq    SEQUENCE     �   ALTER TABLE public.resumes ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.resume_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            public          postgres    false    217                      0    17131    resumes 
   TABLE DATA           F   COPY public.resumes (id, title, path, owner_id, language) FROM stdin;
    public          postgres    false    217   5                 0    17106    users 
   TABLE DATA           �   COPY public.users (id, username, age, email, password, role, message, avatar_title, activation_code, is_in_recovering, resume_pass_key) FROM stdin;
    public          postgres    false    215   R                  0    0    person_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.person_id_seq', 38, true);
          public          postgres    false    214                       0    0    resume_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.resume_id_seq', 67, true);
          public          postgres    false    216            y           2606    17117    users person_email_key 
   CONSTRAINT     R   ALTER TABLE ONLY public.users
    ADD CONSTRAINT person_email_key UNIQUE (email);
 @   ALTER TABLE ONLY public.users DROP CONSTRAINT person_email_key;
       public            postgres    false    215            {           2606    17113    users person_pkey 
   CONSTRAINT     O   ALTER TABLE ONLY public.users
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);
 ;   ALTER TABLE ONLY public.users DROP CONSTRAINT person_pkey;
       public            postgres    false    215            }           2606    17115    users person_username_key 
   CONSTRAINT     X   ALTER TABLE ONLY public.users
    ADD CONSTRAINT person_username_key UNIQUE (username);
 C   ALTER TABLE ONLY public.users DROP CONSTRAINT person_username_key;
       public            postgres    false    215                       2606    17137    resumes resume_title_key 
   CONSTRAINT     T   ALTER TABLE ONLY public.resumes
    ADD CONSTRAINT resume_title_key UNIQUE (title);
 B   ALTER TABLE ONLY public.resumes DROP CONSTRAINT resume_title_key;
       public            postgres    false    217            �           2606    17138    resumes resume_owner_id_fkey    FK CONSTRAINT     |   ALTER TABLE ONLY public.resumes
    ADD CONSTRAINT resume_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id);
 F   ALTER TABLE ONLY public.resumes DROP CONSTRAINT resume_owner_id_fkey;
       public          postgres    false    217    3451    215                  x������ � �            x������ � �     