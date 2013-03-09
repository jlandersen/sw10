/* objective function */
max: bb0 bb1 bb2 bb3 bb4 bb5;

/* flow constraints */
f0 = 1;
f0 = f1;
f1 = f2;
f2 = f3;
f3 = ft0;
ft0 = 1;


/* allocation constraints */
bb0 = 0 f0;
bb1 = 0 f0;
bb2 = 0 f1;
bb3 = 0 f2;
bb4 = 0 f3;
bb5 = 0 ft0;
