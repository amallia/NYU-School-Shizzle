#include "Linker.h"

ifstream stream;

Linker::Linker(char *filename){
        StartLinker(filename);
}

void Linker::StartLinker(char *filename){
    m_input_file_name = filename;
    //ifstream fin = new ifstream();
    //ifstream fin;
    stream.open(filename); //Open File
    if (!stream.good())
        //return 1; // Exit if file not found
        std::cout << "Could not open File: " << filename << std::endl;

     //Parse Module
    ParseOneSetUp();
}

//Getters and Setters
void Linker::SetDefList(vector<Symbol> def_list){
    m_entire_def_list = def_list;
}

vector<Symbol> Linker::GetDefList(){
  return m_entire_def_list; 
}

void Linker::SetModulesList(vector<Module> modules_list){
   m_modules_list = modules_list;
}

vector<Module> Linker::GetModList(){
  return m_modules_list;
}

char* Linker::GetInputFileName(){
    return m_input_file_name;
}

// Functionality
void Linker::ParseOneSetUp(){
    ParseOneModule(0);
    int nextAddress;
    while(!stream.eof())
    {
        ReadUntilModule();
        nextAddress = 1 + m_modules_list.back().GetGlobalAddress() + 
          m_modules_list.back().GetNumberOfLines(); 
        ParseOneModule(nextAddress);
    }
    //Print Symbol Table and Start Parse two
}

void Linker::ParseOneModule(int global_address){
    char c;
    int count; 
    string first_symbol_name;

    // Set up Module
    Module* TempModulePointer = new Module(global_address);
    TempModulePointer->SetGlobalAddress(global_address);
    c = stream.peek();
    if (isdigit(c))
    {
        count = ExtractNumber();
        first_symbol_name = ExtractSymbolName();
        if (count > 16)
        {
            //return 1;  // Defcount or Usecount needs to be less that 16
        } 
         
        c = stream.peek(); 
        if (isdigit(c))
        {
            // We now know this is a deflist 
            ParseOneDefList(count, first_symbol_name, TempModulePointer);

            // Set up Parse Use List
            count = ExtractNumber();
            first_symbol_name = ExtractSymbolName();
        }
        ParseOneUseList(count, first_symbol_name, TempModulePointer); 
        ParseOneOperationList(TempModulePointer); 
        // Print Module this far 
        TempModulePointer->PrintCurrentStatus();
    }
    else
    {
        // return 1;  // This needs to be a number for defcount or usecount
    } 

    // We have now finished module
    m_modules_list.push_back(*TempModulePointer);
    delete TempModulePointer;
}

int Linker::ExtractNumber() {
    string number;
    char c;
    ReadUntilCharacter();

    while (true)
    {
        stream.get(c);
        if ((c != ' ') && (c != '\t') && (c != '\n'))
            number += c; 
        else
            // Need to do checks to make sure number is appropriate
            //return std::stoi(number);
            return atoi(number.c_str());
    }
}

//Extracts Symbol Name
string Linker::ExtractSymbolName() {
    string SymbolName;
    char c;
    ReadUntilCharacter();

    while (true)
    {
        stream.get(c);
        if ((c != ' ') && (c != '\t') && (c != '\n'))
        {    
            SymbolName += c; 
        }
        else
        {
            return SymbolName;
        }
    }
}

char Linker::ExtractOpType(){
    char type;
    ReadUntilCharacter();
    stream.get(type);
    if ((type != 'I') || (type != 'A') || (type != 'R') || (type != 'E'))
    {
        //Error with Type Extraction
    }
    return type;
}
// Stream points after read first symbol name in DefList
void Linker::ParseOneDefList(int count, string first_symbol_name, Module *ModPointer) {
    string symbol_name;
    int relative_address;
    int symbol_absolute_address; 
    // For the amount of defcount
    for (int i = 0; i < count; i++)
    {
        if (i == 0)
        {
            symbol_name = first_symbol_name;
        }
        else
        {
            symbol_name = ExtractSymbolName();
        }
        // Give Exact Address based on Module address
        relative_address =  ExtractNumber();
        symbol_absolute_address = relative_address + ModPointer->GetGlobalAddress();
        Symbol temp_symbol (symbol_name, symbol_absolute_address);
        ModPointer->AddToDefList(temp_symbol);
    }
}

// Stream points after read first symbol name in DefList
void Linker::ParseOneUseList(int count, string first_symbol_name, Module *ModPointer) {
    string symbol_name;
    int relative_address;
    
    // For the amount of defcount
    for (int i = 0; i < count; i++)
    {
        if (i == 0)
        {
            symbol_name = first_symbol_name;
        }
        else
        {
            symbol_name = ExtractSymbolName();
        }
        
        //Need to add these for parse 2, not parse 1
        //Symbol temp_symbol (symbol_name);
        //ModPointer->AddToUseList(temp_symbol);
    }
}

void Linker::ParseOneOperationList(Module *ModPointer) {
    int codecount = ExtractNumber();
    char type;
    int instruction;
    ModPointer->SetNumberOfLines(codecount);
    for (int i = 0; i < codecount; i++)
    {
        type = ExtractOpType();
        instruction = ExtractNumber();
    } 
}

void Linker::ReadUntilCharacter(){
    char c;
    while(true)
    {
        c = stream.peek();
        if ((c == ' ') || (c == '\t') || (c == '\n'))
        {
            stream.get(c);
        }
        else
            return;
    }
}

void Linker::ReadUntilModule(){
    char c;
    while(true)
    {
        c = stream.peek();
        if ((c == ' ') || (c == '\t') || (c == '\n') || (c == '0'))
        {
            stream.get(c);
        }
        else
            return;
    }
}
