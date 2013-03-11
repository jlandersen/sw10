/* objective function */
max: bb0 bb1 bb2 bb3;

/* flow constraints */
f0 = 1;
f0 = f1 + ft0;
f1 = ft1;
ft0 + ft1 = 1;


/* allocation constraints */
bb0 = 0 f0;
bb1 = 0 f0;
bb2 = 0 f1;
bb3 = 0 ft0 + 0 ft1;
