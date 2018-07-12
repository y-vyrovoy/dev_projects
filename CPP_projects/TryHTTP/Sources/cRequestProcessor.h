
#ifndef CREQUESTPROCESSOR_H
#define CREQUESTPROCESSOR_H

#include <vector>

#include "RequestDataTypes.h"

class cRequestProcessor 
{

public:
    cRequestProcessor();
    virtual ~cRequestProcessor();

    int GetResponse(const REQUEST_PARAMS &, std::vector<char>&);
private:
    
    void InitFakeResponse();
    std::vector<char> m_vecResponceBuffer;
};

#endif /* CREQUESTPROCESSOR_H */

