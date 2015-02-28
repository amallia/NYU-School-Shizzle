#ifndef Operation_H
#define Operation_H

#include <string>
#include <iostream>

class Operation
{
    private:
        char m_type;
        int m_instruction;
        int m_absolute_address;

    public:
        Operation();
        Operation(char type, int instruction, int absolute_address);

        void SetType(char type);
        char GetType();

        void SetInstruction(int instruction);
        int GetInstruction();

        void SetAbsoluteAddress(int address);
        int GetAbsoluteAddress();

        void PrintInfo();
};

#endif
