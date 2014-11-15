/*
 * implementation of stdlib.h of the c standard-lib
 *
 * http://www.cplusplus.com/reference/cstdlib/
 */

//------------------- Forward declarations

string library itoa(int x);
string library ftoa(float f);

int library atoi(string s);
//float library atof(string s);

//------------------- declarations

/** convert int to string
 *
 * @working yes
 */
string library itoa(int x) {
    string s = "";
    char ch;
    int y = x;
      
    if(x == 0) {
        return "0";
    } else {
        // y has to be positive
        if(y < 0) {
            y *= -1;
        }
        // calculate characters
        while(y != 0) {
            ch = (char)((y%10)+'0');
            s = ch + s;
            y /= 10;
        }
        // add minus-sign at begin of string if required
        if(x < 0) {
            s = '-' + s;
        }
    }
    
    // return string
    return s;
}

/** convert float to string
 *
 * @working yes
 * @todo using exponential formating
 */
string library ftoa(float f) {
    // caluclate decimal
    string s = itoa((int) f);
    s += '.';
    // f has to be positive
	if(f < 0) {
	    f *= -1;
	}
    // calculate decimal places
    int n = 0;
    while(n <= 4) {
        f -= (int)f;
        // get next decimal place
        f *=10;
        s += itoa((int) f);
        n += 1;
    }
    	
    // return string
    return s;
}

/** convert string to int 
 *
 * @working yes
 */
int library atoi(string s) {
    int ret = 0;
    int i = 0;
    int multpl = 1;
    
    // parse character
    while(length(s) > i) {
        if(s[i] >= '0' && s[i] <= '9') {
            // parse digit
            ret *= 10;
            ret += s[i]-'0';
        } else if(s[i] == '-') {
            // parse '-' character
            multpl *= -1;
        }
        i += 1;
    }
    
    // return integer
    return ret * multpl;
}


// convert string to float
/*float library atof(string s) {
    // TODO
    float ret = 0.;
    int i = 0;
    int afterPoint = 0;

    while(length(s) < i) {
        if(s[i] >= '0' && s[i] <= '9') {
            ret *= 10;
            ret += s[i]-'0';
        } else if(s[i] == '.') {
            afterPoint = 1;
        }
        i += 1;
    }
    return ret;
}*/
