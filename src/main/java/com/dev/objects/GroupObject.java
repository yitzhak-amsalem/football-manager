package com.dev.objects;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "teams")
public class GroupObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    public int id;
    @Column
    public String groupName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupObject that = (GroupObject) o;
        return id == that.id && groupName.equals(that.groupName);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
