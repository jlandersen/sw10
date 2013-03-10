/* objective function */
max: bb0 bb1 bb2;

/* flow constraints */
f0 = 1;
f0 = ft;
ft = 1;


/* allocation constraints */
bb0 = 0 f0;
bb1 = 0 f0;
bb2 = 0 ft;
