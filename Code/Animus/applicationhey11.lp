/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7 bb8 bb9 bb10 bb11 bb12 bb13;

/* flow constraints */
f0 = 1;
f0 = f1 + ft0;
f1 = f2 + ft1;
f2 = f3;
f9 = f4 + ft2;
f4 = f5 + ft3;
f5 = f6 + ft4;
f6 = f7 + ft5;
f7 = f8;
f3 + f8 = f9 + f10;
f10 = f11 + ft6;
f11 = f12 + ft7;
f12 = ft8;
ft0 + ft1 + ft2 + ft3 + ft4 + ft5 + ft6 + ft7 + ft8 = 1;

/* loop constraints */
f9 = 20 f3;

/* allocation constraints */
bb0 = 0 f0;
bb1 = 1 f0;
bb2 = 0 f1;
bb3 = 0 f2;
bb4 = 1 f9;
bb5 = 0 f4;
bb6 = 1 f5;
bb7 = 0 f6;
bb8 = 0 f7;
bb9 = 0 f3 + 0 f8;
bb10 = 1 f10;
bb11 = 0 f11;
bb12 = 0 f12;
bb13 = 0 ft0 + 0 ft1 + 0 ft2 + 0 ft3 + 0 ft4 + 0 ft5 + 0 ft6 + 0 ft7 + 0 ft8;
