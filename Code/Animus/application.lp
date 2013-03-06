/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7 bb8 bb9 bb10 bb11 bb12 bb13 bb14 bb15 bb16 bb17;

/* flow constraints */
f0 = 1;
f0 = f1;
f1 = f2;
f2 = f3;
f3 = f4 + f5;
f5 = f6;
f10 = f7;
f7 = f8;
f8 = f9;
f6 + f9 = f10 + f11;
f11 = f12;
f4 = f13;
f13 = f14;
f14 = f15;
f15 = f16;
f16 = f17;
f12 + f17 = ft;
ft = 1;

/* loop constraints */
f10 = 20 f6;

/* allocation constraints */
bb0 = 0 f0;
bb1 = 0 f0;
bb2 = 0 f1;
bb3 = 0 f2;
bb4 = 0 f3;
bb5 = 0 f5;
bb6 = 1 f10;
bb7 = 0 f7;
bb8 = 0 f8;
bb9 = 0 f6 + 0 f9;
bb10 = 0 f11;
bb11 = 1 f4;
bb12 = 0 f13;
bb13 = 1 f14;
bb14 = 0 f15;
bb15 = 0 f16;
bb16 = 0 f12 + 0 f17;
bb17 = 0 ft;
