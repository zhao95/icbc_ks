create or replace view TS_KCGL_ZWDYB_V as
select t.*,substring_index(t.ZW_ZWH_XT,"-",1) as sort1,substring_index(t.ZW_ZWH_XT,"-",-1) as sort2 from TS_KCGL_ZWDYB t;