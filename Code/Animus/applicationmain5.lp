/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7 bb8 bb9 bb10 bb11 bb12 bb13 bb14 bb15;

/* flow constraints */
f0 = 1;
f0 = f1 + ft0;
f1 = f2 + f3;
f3 = f4;
f8 = f5 + ft1;
f5 = f6 + ft2;
f6 = f7;
f4 + f7 = f8 + f9;
f9 = f10;
f2 = f11 + ft3;
f11 = f12 + ft4;
f12 = f13 + ft5;
f13 = f14 + ft6;
f14 = f15;
f10 + f15 = ft7;
ft0 + ft1 + ft2 + ft3 + ft4 + ft5 + ft6 + ft7 = 1;

/* loop constraints */
f8 = 20 f4;

/* allocation constraints */
bb0 = 0 f0;
bb1 = 1043 f0;
bb2 = 0 f1;
bb3 = 0 f3;
bb4 = 1 f8;
bb5 = 0 f5;
bb6 = 0 f6;
bb7 = 0 f4 + 0 f7;
bb8 = 0 f9;
bb9 = 1 f2;
bb10 = 0 f11;
bb11 = 1 f12;
bb12 = 0 f13;
bb13 = 0 f14;
bb14 = 0 f10 + 0 f15;
bb15 = 0 ft0 + 0 ft1 + 0 ft2 + 0 ft3 + 0 ft4 + 0 ft5 + 0 ft6 + 0 ft7;
