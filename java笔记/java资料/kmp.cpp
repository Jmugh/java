#include<iostream>
#include<string>

using namespace std;

vector<int> getNext(string str){
    if(str.length()==1){
        vector<int> next(str.length()+1,-1);
        return next;
    }
    vector<int> next(str.length()+1,-1);
    next[1]=0;
    int i=2;
    int cn=0;
    while(i<next.size()){
        if(str[i-1]==str[cn]){
            next[i++]=++cn;
        }else if(cn>0){
            cn=next[cn];
        }else{
            next[i++]=0;
        }
    }
    return next;
}
int get_indexof(string str1,string str2){
    if(str1.length()==0||str2.length()==0 || str1.length()<str2.length()){
        return -1;
    }
    int i1=0;
    int i2=0;
    vector<int> next=getNext(str2);
    while(i1<str1.length() && i2<str2.length()){
        if(str1[i1]==str2[i2]){
            i1++;
            i2++;
        }else if(next[i2]==-1){
            i1++;
        }else{
             i2=next[i2];
        }
    }
}


int main(){
	string str1="abcdababca";
	string str2="cababc";
	cout<<get_indexof(str1,str2);
	return 0;
	
}


