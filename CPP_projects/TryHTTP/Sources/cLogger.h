
#ifndef CLOGGER_H
#define CLOGGER_H

#include <iostream>
#include <ostream>


    
class cLogger
{
public:
    
    template <typename T>
    friend cLogger & operator << (cLogger &, const T & param);
    friend cLogger& operator<<(cLogger& os, std::ostream& (*)(std::ostream&) pf);
    
private:

};

template <typename T>
cLogger & operator << (cLogger &, const T & param){std::cout << param;};



cLogger& operator<<(cLogger& os, std::ostream& (*)(std::ostream&) pf)
{
   return operator<< <std::ostream& (*)(std::ostream&)> (os, pf);
}


/// The same code but with typedef

//typedef std::ostream& (*ostream_manipulator)(std::ostream&);
//cLogger& operator<<(cLogger& os, ostream_manipulator pf)
//{
//   return operator<< <ostream_manipulator> (os, pf);
//}

#endif /* CLOGGER_H */

