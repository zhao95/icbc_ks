rem 打was全量包 bat /*工行生产环境打包*/
cd ..

call build_compile.bat

call build_war_icbc_pro.bat

call build_copy_war_icbc.bat