package faang.school.accountservice.entity.account;

public enum Owner {
    USER,
    PROJECT;

    public static Owner getOwnerById(long ownerId){
        for (Owner owner : Owner.values()){
            if(owner.ordinal()==ownerId){
                return owner;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid ownerId: %d", ownerId));
    }

}
