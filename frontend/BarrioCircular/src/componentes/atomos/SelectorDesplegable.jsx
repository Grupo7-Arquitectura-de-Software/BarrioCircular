"use client"
import {Portal, Select} from "@chakra-ui/react"

const SelectorDesplegable = ({titulo, colecciondeDatos}) => {
    return (
        <Select.Root collection={colecciondeDatos} size="sm" width="320px">
            <Select.HiddenSelect/>
            <Select.Label>{titulo}</Select.Label>
            <Select.Control>
                <Select.Trigger>
                    <Select.ValueText placeholder={titulo}/>
                </Select.Trigger>
                <Select.IndicatorGroup>
                    <Select.Indicator/>
                </Select.IndicatorGroup>
            </Select.Control>
            <Portal>
                <Select.Positioner>
                    <Select.Content>
                        {colecciondeDatos.items.map((dato) => (
                            <Select.Item item={dato} key={dato.value}>
                                {dato.label}
                                <Select.ItemIndicator/>
                            </Select.Item>
                        ))}
                    </Select.Content>
                </Select.Positioner>
            </Portal>
        </Select.Root>
    );
};

export default SelectorDesplegable;