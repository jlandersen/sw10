/* objective function */
max: bb0 bb1 bb2 bb3;

/* flow constraints */
f0 = 1;
f0 = f1;
f1 = ft;
ft = 1;


/* allocation constraints */
bb0 = 0 f0;
bb1 = 0 f0;
bb2 = 0 f1;
bb3 = 0 ft;
