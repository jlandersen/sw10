/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5 bb6 bb7;

/* flow constraints */
f0 = 1;
f0 = f1;
f1 = f2;
f2 = f3;
f3 = f4;
f4 = f5;
f5 = ft;
ft = 1;


/* allocation constraints */
bb0 = 0 f0;
bb1 = 1 f0;
bb2 = 0 f1;
bb3 = 1 f2;
bb4 = 0 f3;
bb5 = 0 f4;
bb6 = 0 f5;
bb7 = 0 ft;
