#include "restar.h"


void restar::procRestar() {
	if (valid1 && valid2) {
        dataOut.write(minuendo - sustraendo);
        validOut.write(true);
		// cout << "Resta: " << minuendo << " - " << sustraendo << " = " << minuendo - sustraendo << endl;

		next.write(true);
	}
	else {
        dataOut.write(0);
        validOut.write(false);
        next.write(false);
	}
}



void restar::registers() {

	if (reset.read()) {
		minuendo = sustraendo = 0;
		valid1 = valid2 = false;

	}
	else {
		if (validIn1.read()) {
            minuendo = dataIn1.read();
            valid1 = true;
        }

        if (validIn2.read()) {
            sustraendo = dataIn2.read();
            valid2 = true;
        }

        if (valid1 && valid2 && validOut.read()) {
            valid1 = valid2 = false;
        }
	}

	FIRE.write(!FIRE.read());

}