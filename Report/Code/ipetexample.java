	private void someRealTimeTask() {
BB1:	int y = 0;
 		int j = 0;

BB2:	while(j < 20) { //@WCA loop <= 20
BB3:		for(int i = 0; i < 50; i++) { //@WCA loop <= 50
BB4:			if (i == 42) {
BB5:				y = 42;
				}
				else {
BB6:				someCalculation();
				}
			}
BB7:		j = j + 1;
		}
	}