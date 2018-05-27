#ifndef CBLOCKINGQUEUE_H
#define CBLOCKINGQUEUE_H

#include <mutex>
#include <deque>
#include <condition_variable>

template <typename T>
class cBlockingQueue
{
public:
    
    void push(const T & param)
    {
        {
            std::unique_lock<std::mutex> lock(m_mut);
            m_deque.push_front(param);
        }
        m_cv.notify_all();
    }
    
    T pull()
    {
        std::unique_lock<std::mutex> lock(m_mut);
        m_cv.wait(lock, [=](){return !this->m_deque.empty();});
        T ret(std::move(m_deque.back()));
        m_deque.pop_back();
        return ret;
    }

    bool isEmpty() const
    {
        return m_deque.empty();
    }
    
private:
    std::mutex m_mut;
    std::deque<T> m_deque;
    std::condition_variable m_cv;
        
};

#endif /* CBLOCKINGQUEUE_H */

