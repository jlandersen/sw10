/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7 bb8 bb9 bb10 bb11 bb12 bb13 bb14 bb15 bb16 bb17;

/* flow constraints */
f0 = 1;
f0 = f1 + f2 + ft0;
f1 = f3;
f2 = f4;
f10 = f5 + ft1;
f5 = f6 + ft2;
f6 = f7 + ft3;
f7 = f8 + ft4;
f8 = f9;
f4 + f9 = f10 + f11;
f3 + f11 = f12 + f13;
f13 = f14 + ft5;
f14 = f15 + ft6;
f15 = ft7;
f12 = f16 + ft8;
f16 = f17 + ft9;
f17 = ft10;
ft0 + ft1 + ft2 + ft3 + ft4 + ft5 + ft6 + ft7 + ft8 + ft9 + ft10 = 1;

/* loop constraints */
f10 = 500 f4;

/* allocation constraints */
bb0 = 0 f0;
bb1 = 42 f0;
bb2 = 0 f1;
bb3 = 0 f2;
bb4 = 1 f10;
bb5 = 0 f5;
bb6 = 1 f6;
bb7 = 0 f7;
bb8 = 0 f8;
bb9 = 0 f4 + 0 f9;
bb10 = 0 f3 + 0 f11;
bb11 = 1 f13;
bb12 = 0 f14;
bb13 = 0 f15;
bb14 = 1 f12;
bb15 = 0 f16;
bb16 = 0 f17;
bb17 = 0 ft0 + 0 ft1 + 0 ft2 + 0 ft3 + 0 ft4 + 0 ft5 + 0 ft6 + 0 ft7 + 0 ft8 + 0 ft9 + 0 ft10;
